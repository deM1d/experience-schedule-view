package com.idemidov.experiencescheduleview

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

class ExperienceScheduleDayDrawable(
    private val backgroundPaint: Paint,
    private val textPaint: Paint,
    val slots: List<Schedule>?,
) {
    var left = 0f
    var top = 0f
    var right = 0f
    var bottom = 0f
    var textLeft = 0f
    var textBottom = 0f
    var text = ""
    var status = INACTIVE

    fun draw(canvas: Canvas?) {
        backgroundPaint.style = Paint.Style.FILL
        canvas?.drawRect(left, top, right, bottom, backgroundPaint)
        backgroundPaint.style = Paint.Style.STROKE
        backgroundPaint.color = Color.WHITE
        backgroundPaint.strokeWidth = 4f
        canvas?.drawRect(left, top, right, bottom, backgroundPaint)
        canvas?.drawText(text, textLeft, textBottom, textPaint)
    }

    fun isClicked(x: Float, y: Float) = x > left && x < right && y > top && y < bottom

    fun setBackgroundColor(color: Int) {
        backgroundPaint.color = color
    }

    fun setTextColor(color: Int) {
        textPaint.color = color
    }

    companion object {
        const val AVAILABLE = 0
        const val BOOKED = 1
        const val INACTIVE = 2
    }
}