package com.idemidov.experiencescheduleview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector.OnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.GestureDetectorCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import java.lang.Integer.max
import java.util.*
import kotlin.math.min

class ExperienceScheduleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val yearPaint = Paint()
    private val monthPaint = Paint()
    private val daysPaint = Paint()
    private val slotPaint = Paint()
    private val slotTextPaint = Paint()

    private val yearRect = Rect()
    private val monthRect = Rect()
    private val dayRect = Rect()
    private val slotRect = Rect()

    private val colorPalette = ExperiencesScheduleColorPalette()
    private val calendarWrapper = ExperienceScheduleCalendar()

    private val yearDrawable = ExperienceScheduleTextDrawable(yearPaint)
    private val monthDrawable = ExperienceScheduleTextDrawable(monthPaint)
    private val daysDrawables = arrayListOf<ExperienceScheduleTextDrawable>()
    private val leftButton = ExperienceScheduleOvalDrawable(
        AppCompatResources.getDrawable(
            context,
            R.drawable.experience_slot_left
        )!!
    )
    private val rightButton = ExperienceScheduleOvalDrawable(
        AppCompatResources.getDrawable(
            context,
            R.drawable.experience_slot_right
        )!!
    )
    private val leftMonth = arrayListOf<ExperienceScheduleDayDrawable>()
    private val centerMonth = arrayListOf<ExperienceScheduleDayDrawable>()
    private val rightMonth = arrayListOf<ExperienceScheduleDayDrawable>()

    private var schedule: ExperienceSchedule<Schedule>? = null
    private var scrollX = 0f
    private var animationIsRunning = false
    private var buttonSize = 0f
    private var slotHeight = 0f

    private var scheduleClickListener: ExperienceScheduleClickListener? = null

    private val months = arrayListOf(
        "Январь", "Февраль", "Март", "Апрель",
        "Май", "Июнь", "Июль", "Август",
        "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    )

    private val days = arrayListOf(
        "Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"
    )

    init {
        context.withStyledAttributes(attrs, R.styleable.ExperienceSlotsView) {
            colorPalette.backgroundColor = getColor(
                R.styleable.ExperienceSlotsView_backgroundColor,
                ContextCompat.getColor(context, R.color.background_default)
            )
            colorPalette.yearColor = getColor(
                R.styleable.ExperienceSlotsView_yearTextColor,
                ContextCompat.getColor(context, R.color.year_text_default)
            )
            colorPalette.monthColor = getColor(
                R.styleable.ExperienceSlotsView_monthTextColor,
                ContextCompat.getColor(context, R.color.month_text_default)
            )
            colorPalette.daysColor = getColor(
                R.styleable.ExperienceSlotsView_daysTextColor,
                ContextCompat.getColor(context, R.color.days_text_default)
            )
            colorPalette.slotBackgroundAvailableColor = getColor(
                R.styleable.ExperienceSlotsView_slotBackgroundAvailableColor,
                ContextCompat.getColor(context, R.color.slot_background_available_default)
            )
            colorPalette.slotBackgroundBookedColor = getColor(
                R.styleable.ExperienceSlotsView_slotBackgroundBookedColor,
                ContextCompat.getColor(context, R.color.slot_background_booked_default)
            )
            colorPalette.slotBackgroundInactiveColor = getColor(
                R.styleable.ExperienceSlotsView_slotBackgroundInactiveColor,
                ContextCompat.getColor(context, R.color.slot_background_inactive_default)
            )
            colorPalette.slotTextAvailableColor = getColor(
                R.styleable.ExperienceSlotsView_slotTextAvailableColor,
                ContextCompat.getColor(context, R.color.slot_text_available_default)
            )
            colorPalette.slotTextBookedColor = getColor(
                R.styleable.ExperienceSlotsView_slotTextBookedColor,
                ContextCompat.getColor(context, R.color.slot_text_booked_default)
            )
            colorPalette.slotTextInactiveColor = getColor(
                R.styleable.ExperienceSlotsView_slotTextInactiveColor,
                ContextCompat.getColor(context, R.color.slot_text_inactive_default)
            )
            yearPaint.textSize = getDimension(
                R.styleable.ExperienceSlotsView_yearTextSize,
                context.resources.getDimension(R.dimen.experience_slot_year_text_size_default)
            )
            monthPaint.textSize = getDimension(
                R.styleable.ExperienceSlotsView_monthTextSize,
                context.resources.getDimension(R.dimen.experience_slot_month_text_size_default)
            )
            daysPaint.textSize = getDimension(
                R.styleable.ExperienceSlotsView_daysTextSize,
                context.resources.getDimension(R.dimen.experience_slot_days_text_size_default)
            )
            slotTextPaint.textSize = getDimension(
                R.styleable.ExperienceSlotsView_slotTextSize,
                context.resources.getDimension(R.dimen.experience_slot_slot_text_size_default)
            )
            buttonSize = getDimension(
                R.styleable.ExperienceSlotsView_buttonsSize,
                context.resources.getDimension(R.dimen.experience_slot_button_size_default)
            )
            slotHeight = getDimension(
                R.styleable.ExperienceSlotsView_slotsHeight,
                context.resources.getDimension(R.dimen.experience_slot_height_default)
            )
        }

        for (day in days) {
            daysDrawables.add(ExperienceScheduleTextDrawable(daysPaint).apply { text = day })
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val width = measuredWidth
        val height = measuredHeight

        canvas?.drawColor(colorPalette.backgroundColor)

        yearDrawable.text = calendarWrapper.currentYear.toString()
        yearDrawable.left = width / 2f - yearRect.width() / 2f
        yearDrawable.top = PADDING
        yearDrawable.right = yearDrawable.left + yearRect.width()
        yearDrawable.bottom =
            yearDrawable.top + (yearRect.height() / 2) - (yearPaint.fontMetrics.ascent / 2)
        yearDrawable.draw(canvas)

        val monthHeight = measureMonthHeight()
        monthDrawable.text = months[calendarWrapper.currentMonth]
        monthDrawable.left = width / 2f - monthRect.width() / 2f
        monthDrawable.top = PADDING * 2 + yearRect.height()
        monthDrawable.right = monthDrawable.left + monthRect.width()
        monthDrawable.bottom =
            monthDrawable.top + (monthHeight / 2) - (monthPaint.fontMetrics.ascent / 2)
        monthDrawable.draw(canvas)

        leftButton.left = PADDING
        leftButton.top = monthDrawable.bottom / 2 - buttonSize / 2
        leftButton.right = leftButton.left + buttonSize
        leftButton.bottom = leftButton.top + buttonSize
        leftButton.draw(canvas)

        rightButton.left = width - PADDING - buttonSize
        rightButton.top = monthDrawable.bottom / 2 - buttonSize / 2
        rightButton.right = rightButton.left + buttonSize
        rightButton.bottom = rightButton.top + buttonSize
        rightButton.draw(canvas)

        val daysHeight = measureDaysHeight()
        val slotWidth = width.toFloat() / 7
        val daysBottom = PADDING * 3 + yearRect.height() + monthHeight + daysHeight
        daysDrawables.forEachIndexed { index, dayDrawable ->
            daysPaint.getTextBounds(dayDrawable.text, 0, dayDrawable.text.length, dayRect)
            dayDrawable.left = (slotWidth * index.toFloat()) + (slotWidth / 2 - dayRect.width() / 2)
            dayDrawable.top = PADDING * 3 + yearRect.height() + monthHeight
            dayDrawable.right = dayDrawable.left + dayRect.width()
            dayDrawable.bottom =
                dayDrawable.top + (daysHeight / 2 - daysPaint.fontMetrics.ascent / 2)
            dayDrawable.draw(canvas)
        }

        calendarWrapper.decreaseCurrentMonth()
        var weeksCountInMonth = calendarWrapper.getMaxWeekOfMonth()
        var slotsHeight = height - (daysBottom + PADDING)
        var slotHeight = slotsHeight / weeksCountInMonth

        leftMonth.forEachIndexed { index, slotDrawable ->
            calendarWrapper.setDate(index + 1)
            val dayOfWeek = convertDayNumber(calendarWrapper.getDayOfWeek())
            val weekOfMonth = calendarWrapper.getWeekOfMonth()

            slotDrawable.text = (index + 1).toString()
            slotDrawable.left = slotWidth * (dayOfWeek - 1) + scrollX - width
            slotDrawable.top = daysBottom + PADDING + slotHeight * (weekOfMonth - 1)
            slotDrawable.right = slotDrawable.left + slotWidth
            slotDrawable.bottom = slotDrawable.top + slotHeight
            populateSlotStatus(slotDrawable)

            slotTextPaint.getTextBounds(slotDrawable.text, 0, slotDrawable.text.length, slotRect)
            slotDrawable.textLeft =
                slotDrawable.left + (slotDrawable.right - slotDrawable.left) / 2 - slotRect.width() / 2
            slotDrawable.textBottom =
                slotDrawable.top + (slotDrawable.bottom - slotDrawable.top) / 2 - slotTextPaint.fontMetrics.ascent / 2

            slotDrawable.draw(canvas)
        }

        calendarWrapper.increaseCurrentMonth()
        weeksCountInMonth = calendarWrapper.getMaxWeekOfMonth()
        slotsHeight = height - (daysBottom + PADDING)
        slotHeight = slotsHeight / weeksCountInMonth

        centerMonth.forEachIndexed { index, slotDrawable ->
            calendarWrapper.setDate(index + 1)
            val dayOfWeek = convertDayNumber(calendarWrapper.getDayOfWeek())
            val weekOfMonth = calendarWrapper.getWeekOfMonth()

            slotDrawable.text = (index + 1).toString()
            slotDrawable.left = slotWidth * (dayOfWeek - 1) + scrollX
            slotDrawable.top = daysBottom + PADDING + slotHeight * (weekOfMonth - 1)
            slotDrawable.right = slotDrawable.left + slotWidth
            slotDrawable.bottom = slotDrawable.top + slotHeight
            populateSlotStatus(slotDrawable)

            slotTextPaint.getTextBounds(slotDrawable.text, 0, slotDrawable.text.length, slotRect)
            slotDrawable.textLeft =
                slotDrawable.left + (slotDrawable.right - slotDrawable.left) / 2 - slotRect.width() / 2
            slotDrawable.textBottom =
                slotDrawable.top + (slotDrawable.bottom - slotDrawable.top) / 2 - slotTextPaint.fontMetrics.ascent / 2

            slotDrawable.draw(canvas)
        }

        calendarWrapper.increaseCurrentMonth()
        weeksCountInMonth = calendarWrapper.getMaxWeekOfMonth()
        slotsHeight = height - (daysBottom + PADDING)
        slotHeight = slotsHeight / weeksCountInMonth

        rightMonth.forEachIndexed { index, slotDrawable ->
            calendarWrapper.setDate(index + 1)
            val dayOfWeek = convertDayNumber(calendarWrapper.getDayOfWeek())
            val weekOfMonth = calendarWrapper.getWeekOfMonth()

            slotDrawable.text = (index + 1).toString()
            slotDrawable.left = slotWidth * (dayOfWeek - 1) + scrollX + width
            slotDrawable.top = daysBottom + PADDING + slotHeight * (weekOfMonth - 1)
            slotDrawable.right = slotDrawable.left + slotWidth
            slotDrawable.bottom = slotDrawable.top + slotHeight
            populateSlotStatus(slotDrawable)

            slotTextPaint.getTextBounds(slotDrawable.text, 0, slotDrawable.text.length, slotRect)
            slotDrawable.textLeft =
                slotDrawable.left + (slotDrawable.right - slotDrawable.left) / 2 - slotRect.width() / 2
            slotDrawable.textBottom =
                slotDrawable.top + (slotDrawable.bottom - slotDrawable.top) / 2 - slotTextPaint.fontMetrics.ascent / 2

            slotDrawable.draw(canvas)
        }

        calendarWrapper.decreaseCurrentMonth()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val yearHeight = measureYearHeight()

        val monthHeight = measureMonthHeight()

        var daysWidth = (days.size + 1) * PADDING
        var daysHeight = 0
        days.forEach { day ->
            daysPaint.getTextBounds(day, 0, day.length, dayRect)
            daysWidth += dayRect.width()
            daysHeight = max(daysHeight, dayRect.height())
        }

        val measuredWidth = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> {
                min(widthSize, daysWidth.toInt())
            }
            else -> daysWidth.toInt()
        }

        val contentHeight =
            (yearHeight + monthHeight + daysHeight + (6 * slotHeight) + (PADDING * 5)).toInt()
        val measuredHeight = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> {
                min(heightSize, contentHeight)
            }
            else -> contentHeight
        }

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (detector.onTouchEvent(event)) {
            performClick()
            true
        } else {
            super.onTouchEvent(event)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    fun setSchedule(schedule: ExperienceSchedule<Schedule>) {
        this.schedule = schedule
        calendarWrapper.setTime(getBeginTime())
        initSlots()
    }

    fun setOnSlotClickListener(listener: ExperienceScheduleClickListener?) {
        this.scheduleClickListener = listener
    }

    private fun measureYearHeight(): Int {
        yearPaint.getTextBounds(
            calendarWrapper.currentYear.toString(),
            0,
            calendarWrapper.currentYear.toString().length,
            yearRect
        )
        return yearRect.height()
    }

    private fun measureMonthHeight(): Int {
        var monthHeight = 0
        months.forEach { month ->
            monthPaint.getTextBounds(month, 0, month.length, monthRect)
            monthHeight = max(monthHeight, monthRect.height())
        }
        monthPaint.getTextBounds(
            months[calendarWrapper.currentMonth],
            0,
            months[calendarWrapper.currentMonth].length,
            monthRect
        )
        return monthHeight
    }

    private fun measureDaysHeight(): Int {
        var daysHeight = 0
        daysDrawables.forEach { dayDrawable ->
            daysPaint.getTextBounds(dayDrawable.text, 0, dayDrawable.text.length, dayRect)
            daysHeight = max(daysHeight, dayRect.height())
        }
        return daysHeight
    }

    private fun elementClicked(x: Float, y: Float): Boolean {
        if (leftButton.isClicked(x, y)) {
            slideToLeft()
            return true
        }
        if (rightButton.isClicked(x, y)) {
            slideToRight()
            return true
        }
        for (slot in centerMonth) {
            if (slot.isClicked(x, y) && slot.status == ExperienceScheduleDayDrawable.AVAILABLE) {
                scheduleClickListener?.onSlotClick(slot.slots!!)
                return true
            }
        }
        return false
    }

    private fun slideToLeft() {
        if (calendarWrapper.getTimeInMillis() <= getBeginTime() || animationIsRunning) {
            return
        }
        val animator = ValueAnimator.ofFloat(0f, 100f).apply {
            duration = ANIMATION_DURATION
            interpolator = FastOutSlowInInterpolator()
        }
        animator.addUpdateListener {
            scrollX = measuredWidth.toFloat() * it.animatedValue as Float / 100
            invalidate()
        }
        animator.addListener(onEnd = {
            scrollX = 0f
            calendarWrapper.decreaseCurrentMonth()
            initSlots()
            animationIsRunning = false
            requestLayout()
            invalidate()
        })
        animationIsRunning = true
        animator.start()
    }

    private fun slideToRight() {
        if (calendarWrapper.getTimeInMillis() >= getEndTime() || animationIsRunning) {
            return
        }
        val animator = ValueAnimator.ofFloat(0f, 100f).apply {
            duration = ANIMATION_DURATION
            interpolator = FastOutSlowInInterpolator()
        }
        animator.addUpdateListener {
            scrollX = -(measuredWidth.toFloat() * it.animatedValue as Float / 100)
            invalidate()
        }
        animator.addListener(onEnd = {
            scrollX = 0f
            calendarWrapper.increaseCurrentMonth()
            initSlots()
            animationIsRunning = false
            requestLayout()
            invalidate()
        })
        animationIsRunning = true
        animator.start()
    }

    private fun getBeginTime() = schedule!!.getBegin() * 1000

    private fun getEndTime() = schedule!!.getEnd() * 1000

    private fun convertDayNumber(day: Int): Int {
        val position = day - 1
        return if (position == 0) 7 else position
    }

    private fun initSlots() {
        leftMonth.clear()
        centerMonth.clear()
        rightMonth.clear()

        val slotCalendar = Calendar.getInstance()

        calendarWrapper.decreaseCurrentMonth()
        for (i in 1..calendarWrapper.getMaxDayOfMonth()) {
            calendarWrapper.setDate(i)
            var slots: List<Schedule>? = null
            for (key in schedule!!.getSchedule().keys) {
                slotCalendar.timeInMillis = key * 1000
                if (
                    calendarWrapper.getYear() == slotCalendar.get(Calendar.YEAR) &&
                    calendarWrapper.getMonth() == slotCalendar.get(Calendar.MONTH) &&
                    calendarWrapper.getDayOfMonth() == slotCalendar.get(Calendar.DAY_OF_MONTH)
                ) {
                    slots = schedule!!.getSchedule()[key]
                    break
                }
            }
            leftMonth.add(ExperienceScheduleDayDrawable(slotPaint, slotTextPaint, slots))
        }

        calendarWrapper.increaseCurrentMonth()
        for (i in 1..calendarWrapper.getMaxDayOfMonth()) {
            calendarWrapper.setDate(i)
            var slots: List<Schedule>? = null
            for (key in schedule!!.getSchedule().keys) {
                slotCalendar.timeInMillis = key * 1000
                if (
                    calendarWrapper.getYear() == slotCalendar.get(Calendar.YEAR) &&
                    calendarWrapper.getMonth() == slotCalendar.get(Calendar.MONTH) &&
                    calendarWrapper.getDayOfMonth() == slotCalendar.get(Calendar.DAY_OF_MONTH)
                ) {
                    slots = schedule!!.getSchedule()[key]
                    break
                }
            }
            centerMonth.add(ExperienceScheduleDayDrawable(slotPaint, slotTextPaint, slots))
        }

        calendarWrapper.increaseCurrentMonth()
        for (i in 1..calendarWrapper.getMaxDayOfMonth()) {
            calendarWrapper.setDate(i)
            var slots: List<Schedule>? = null
            for (key in schedule!!.getSchedule().keys) {
                slotCalendar.timeInMillis = key * 1000
                if (
                    calendarWrapper.getYear() == slotCalendar.get(Calendar.YEAR) &&
                    calendarWrapper.getMonth() == slotCalendar.get(Calendar.MONTH) &&
                    calendarWrapper.getDayOfMonth() == slotCalendar.get(Calendar.DAY_OF_MONTH)
                ) {
                    slots = schedule!!.getSchedule()[key]
                    break
                }
            }
            rightMonth.add(ExperienceScheduleDayDrawable(slotPaint, slotTextPaint, slots))
        }

        calendarWrapper.decreaseCurrentMonth()
    }

    private fun populateSlotStatus(slotDrawable: ExperienceScheduleDayDrawable) {
        if (slotDrawable.slots != null) {
            slotDrawable.status = ExperienceScheduleDayDrawable.AVAILABLE
            slotDrawable.setBackgroundColor(colorPalette.slotBackgroundAvailableColor)
            slotDrawable.setTextColor(colorPalette.slotTextAvailableColor)
        } else {
            if (calendarWrapper.getTimeInMillis() < schedule!!.getBegin() * 1000 || calendarWrapper.getTimeInMillis() > schedule!!.getEnd() * 1000) {
                slotDrawable.status = ExperienceScheduleDayDrawable.INACTIVE
                slotDrawable.setBackgroundColor(colorPalette.slotBackgroundInactiveColor)
                slotDrawable.setTextColor(colorPalette.slotTextInactiveColor)
            } else {
                slotDrawable.status = ExperienceScheduleDayDrawable.BOOKED
                slotDrawable.setBackgroundColor(colorPalette.slotBackgroundBookedColor)
                slotDrawable.setTextColor(colorPalette.slotTextBookedColor)
            }
        }
    }

    private val detector = GestureDetectorCompat(context, object : OnGestureListener {
        override fun onDown(e: MotionEvent) = true

        override fun onShowPress(e: MotionEvent) {}

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return elementClicked(e.getX(0), e.getY(0))
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ) = true

        override fun onLongPress(e: MotionEvent) {}

        override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float) =
            true
    })

    companion object {
        private const val PADDING = 32f
        private const val ANIMATION_DURATION = 300L
    }
}