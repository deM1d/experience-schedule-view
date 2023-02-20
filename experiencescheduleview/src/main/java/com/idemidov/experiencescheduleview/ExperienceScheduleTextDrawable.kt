package com.idemidov.experiencescheduleview

import android.graphics.Canvas
import android.graphics.Paint

class ExperienceScheduleTextDrawable(
    private val paint: Paint
) {
    var text = ""
    var left = 0f
    var top = 0f
    var right = 0f
    var bottom = 0f

    fun draw(canvas: Canvas?) {
        canvas?.drawText(text, left, bottom, paint)
    }
}