package com.idemidov.experiencescheduleview

import android.graphics.Canvas
import android.graphics.drawable.Drawable

class ExperienceScheduleOvalDrawable(
    private val drawable: Drawable
) {
    var left = 0f
    var top = 0f
    var right = 0f
    var bottom = 0f

    fun draw(canvas: Canvas?) {
        canvas?.let {
            drawable.setBounds(left.toInt(), top.toInt(), right.toInt(), bottom.toInt())
            drawable.draw(canvas)
        }
    }

    fun isClicked(x: Float, y: Float) = x > left && x < right && y > top && y < bottom
}