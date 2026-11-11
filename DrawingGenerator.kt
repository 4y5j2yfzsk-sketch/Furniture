package com.furniture.designer.drawing

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.furniture.designer.data.Cabinet
import com.furniture.designer.data.CuttingResult

object DrawingGenerator {
    fun drawAssembly(canvas: Canvas, cabinet: Cabinet, paint: Paint) {
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 3f
        val scale = minOf(
            canvas.width * 0.65f / cabinet.width,
            canvas.height * 0.65f / cabinet.height
        )
        val left = (canvas.width - cabinet.width * scale) / 2f
        val top = (canvas.height - cabinet.height * scale) / 2f
        canvas.drawRect(
            RectF(left, top, left + cabinet.width * scale, top + cabinet.height * scale),
            paint
        )
        val t = cabinet.thickness * scale
        canvas.drawRect(left + t, top + t,
            left + cabinet.width * scale - t, top + cabinet.height * scale - t, paint)
        if (cabinet.shelves > 0) {
            val innerTop = top + t
            val innerHeight = (cabinet.height * scale - 2 * t)
            for (i in 1..cabinet.shelves) {
                val y = innerTop + innerHeight * i / (cabinet.shelves + 1)
                canvas.drawLine(left + t, y, left + cabinet.width * scale - t, y, paint)
            }
        }
        paint.style = Paint.Style.FILL
    }

    fun drawCuttingMap(canvas: Canvas, result: CuttingResult, sheetIndex: Int, paint: Paint) {
        if (sheetIndex !in result.sheets.indices) return
        val sheet = result.sheet
        val margin = 24f
        val scale = minOf(
            (canvas.width - margin * 2) / sheet.width,
            (canvas.height - margin * 2) / sheet.height
        )
        val left = (canvas.width - sheet.width * scale) / 2f
        val top = (canvas.height - sheet.height * scale) / 2f
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        canvas.drawRect(left, top, left + sheet.width * scale, top + sheet.height * scale, paint)
        paint.style = Paint.Style.FILL
        for ((index, placed) in result.sheets[sheetIndex].withIndex()) {
            val x = left + placed.x * scale
            val y = top + placed.y * scale
            val w = placed.placedWidth * scale
            val h = placed.placedHeight * scale
            paint.style = Paint.Style.STROKE
            canvas.drawRect(x, y, x + w, y + h, paint)
            paint.style = Paint.Style.FILL
            paint.textSize = 22f
            canvas.drawText("${index + 1}", x + 5f, y + 25f, paint)
        }
    }
}
