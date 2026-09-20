package com.bit.v21remote

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DpadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    var paint: Paint = Paint()
    var pressed: String = ""

    init {
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width
        val h = height
        val cx = w / 2
        val cy = h / 2
        val radius = Math.min(w, h) / 2 - 20
        val gap = radius / 2

        paint.isAntiAlias = true

        // Outer circle
        paint.color = Color.parseColor("#E0E0E0") // outer
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), paint)

        // Center circle
        paint.color = Color.parseColor("#2A2A2A") // center
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), (radius / 3).toFloat(), paint)

        // Arrow text
        paint.color = Color.parseColor("#555555") // arrows
        paint.textSize = 60f
        paint.textAlign = Paint.Align.CENTER

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4f
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), paint)

        paint.style = Paint.Style.FILL

        canvas.drawText("▲", cx.toFloat(), (cy - 150).toFloat(), paint)
        canvas.drawText("▼", cx.toFloat(), (cy + 200).toFloat(), paint)
        canvas.drawText("◀", (cx - 150).toFloat(), (cy + 20).toFloat(), paint)
        canvas.drawText("▶", (cx + 150).toFloat(), (cy + 20).toFloat(), paint)

        paint.color = Color.parseColor("#33FFFFFF")

        if (pressed == "UP")
            canvas.drawCircle(cx.toFloat(), (cy - gap).toFloat(), 60f, paint)

        if (pressed == "DOWN")
            canvas.drawCircle(cx.toFloat(), (cy + gap).toFloat(), 60f, paint)

        if (pressed == "LEFT")
            canvas.drawCircle((cx - gap).toFloat(), cy.toFloat(), 60f, paint)

        if (pressed == "RIGHT")
            canvas.drawCircle((cx + gap).toFloat(), cy.toFloat(), 60f, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        val w = width
        val h = height

        if (event.action == MotionEvent.ACTION_DOWN) {
            pressed = when {
                y < h / 3 -> "UP"
                y > h * 2 / 3 -> "DOWN"
                x < w / 3 -> "LEFT"
                x > w * 2 / 3 -> "RIGHT"
                else -> "CENTER"
            }
            invalidate() // redraw
        }

        return true
    }
}