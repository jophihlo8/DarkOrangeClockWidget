package com.example.clockwidget

import android.content.Context
import android.graphics.*
import kotlin.math.min

object ClockRenderer {

    fun createBackground(
        context: Context,
        size: Int
    ): Bitmap {

        val bitmap =
            Bitmap.createBitmap(
                size,
                size,
                Bitmap.Config.ARGB_8888
            )

        val canvas =
            Canvas(bitmap)

        val radius =
            size / 2f

        val photo =
            PhotoManager.loadPhoto(context)

        if (photo != null) {

            drawPhoto(
                canvas,
                photo,
                size
            )

        } else {

            canvas.drawColor(
                Color.parseColor("#0C121D")
            )

        }

        val overlay =
            Paint(Paint.ANTI_ALIAS_FLAG)

        overlay.color =
            Color.argb(
                70,
                0,
                0,
                0
            )

        canvas.drawCircle(
            radius,
            radius,
            radius,
            overlay
        )

        return bitmap

    }

    private fun drawPhoto(
        canvas: Canvas,
        bitmap: Bitmap,
        size: Int
    ) {

        val src =
            Rect(
                0,
                0,
                bitmap.width,
                bitmap.height
            )

        val dst =
            Rect(
                0,
                0,
                size,
                size
            )

        canvas.drawBitmap(
            bitmap,
            src,
            dst,
            Paint(Paint.ANTI_ALIAS_FLAG)
        )

    }

}
