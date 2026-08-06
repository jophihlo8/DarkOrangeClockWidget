package com.example.clockwidget

import android.content.Context
import android.graphics.*
import kotlin.math.max

object ClockRenderer {

    fun drawBackground(
        context: Context,
        canvas: Canvas,
        size: Int
    ) {

        val photo = PhotoManager.loadPhoto(context)

        if (photo != null) {

            val scaled = Bitmap.createScaledBitmap(
                photo,
                size,
                size,
                true
            )

            canvas.drawBitmap(
                scaled,
                0f,
                0f,
                null
            )

        } else {

            canvas.drawColor(
                Color.parseColor("#0C121D")
            )

        }

        val overlay = Paint(Paint.ANTI_ALIAS_FLAG)

        overlay.color = Color.argb(
            90,
            0,
            0,
            0
        )

        canvas.drawRect(
            0f,
            0f,
            size.toFloat(),
            size.toFloat(),
            overlay
        )

    }

    fun drawOrangeRing(
        canvas: Canvas,
        size: Int
    ) {

        val radius = size / 2f

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.style = Paint.Style.STROKE

        paint.strokeWidth = size * 0.03f

        paint.strokeCap = Paint.Cap.ROUND

        paint.color = Color.parseColor("#FF8C00")

        canvas.drawCircle(
            radius,
            radius,
            radius - paint.strokeWidth,
            paint
        )

    }

    fun drawCenter(
        canvas: Canvas,
        size: Int
    ) {

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.color = Color.parseColor("#FF8C00")

        canvas.drawCircle(
            size / 2f,
            size / 2f,
            size * 0.04f,
            paint
        )

    }

}
