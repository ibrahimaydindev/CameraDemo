package com.example.camerademo

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class OverlayView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var boxes: List<Rect>? = null
    private val boxPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    fun setBoxes(boxes: List<Rect>) {
        this.boxes = boxes
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        boxes?.forEach { canvas.drawRect(it, boxPaint) }
    }

     fun addBox(box: Rect) {
        boxes?.let {
            boxes = it + box
        } ?: run {
            boxes = listOf(box)
        }
        invalidate()
    }
}