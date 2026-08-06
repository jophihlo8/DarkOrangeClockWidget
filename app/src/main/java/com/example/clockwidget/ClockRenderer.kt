package com.example.clockwidget

import android.content.Context
import android.graphics.*
import kotlin.math.max

object ClockRenderer {

    private const val BACKGROUND = "#0C121D"
    private const val RING = "#FF8C00"
    private const val SHADOW = "#33000000"

    fun createBackground(
        context: Context,
        size: Int
    ): Bitmap {

        val bitmap = Bitmap.createBitmap(
            size,
            size,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)

        drawBackground(
            context,
            canvas,
            size
        )

        drawOuterRing(
            canvas,
            size
        )

        drawMinuteMarks(
            canvas,
            size
        )

        drawHourMarks(
            canvas,
            size
        )

        return bitmap
    }

    private fun drawBackground(
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
                Color.parseColor(BACKGROUND)
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

    private fun drawOuterRing(
        canvas: Canvas,
        size: Int
    ) {

        val radius = size / 2f

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.style = Paint.Style.STROKE

        paint.strokeCap = Paint.Cap.ROUND

        paint.strokeWidth = size * 0.03f

        paint.color = Color.parseColor(RING)

        paint.setShadowLayer(
            8f,
            0f,
            0f,
            Color.parseColor(SHADOW)
        )

        canvas.drawCircle(
            radius,
            radius,
            radius - paint.strokeWidth,
            paint
        )

    }

    private fun drawMinuteMarks(
        canvas: Canvas,
        size: Int
    ) {

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.color = Color.WHITE

        paint.strokeCap = Paint.Cap.ROUND

        paint.strokeWidth = max(
            2f,
            size * 0.004f
        )

        val cx = size / 2f
        val cy = size / 2f

        val outer = size * 0.46f
        val inner = size * 0.43f

        for (i in 0 until 60) {

            if (i % 5 == 0)
                continue

            val angle = Math.toRadians(
                (i * 6 - 90).toDouble()
            )

            val sx =
                (cx + inner * kotlin.math.cos(angle)).toFloat()

            val sy =
                (cy + inner * kotlin.math.sin(angle)).toFloat()

            val ex =
                (cx + outer * kotlin.math.cos(angle)).toFloat()

            val ey =
                (cy + outer * kotlin.math.sin(angle)).toFloat()

            canvas.drawLine(
                sx,
                sy,
                ex,
                ey,
                paint
            )

        }

    }

    private fun drawHourMarks(
        canvas: Canvas,
        size: Int
    ) {

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        paint.color = Color.parseColor(RING)

        paint.strokeCap = Paint.Cap.ROUND

        paint.strokeWidth = size * 0.012f

        val cx = size / 2f
        val cy = size / 2f

        val outer = size * 0.46f
        val inner = size * 0.39f

        for (i in 0 until 12) {

            val angle = Math.toRadians(
                (i * 30 - 90).toDouble()
            )

            val sx =
                (cx + inner * kotlin.math.cos(angle)).toFloat()

            val sy =
                (cy + inner * kotlin.math.sin(angle)).toFloat()

            val ex =
                (cx + outer * kotlin.math.cos(angle)).toFloat()

            val ey =
                (cy + outer * kotlin.math.sin(angle)).toFloat()

            canvas.drawLine(
                sx,
                sy,
                ex,
                ey,
                paint
            )

        }

    }

}
