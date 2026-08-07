package com.example.clockwidget

import android.content.Context
import android.graphics.*
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * Draws the dark navy / orange analog clock face into a single square
 * bitmap: background (photo or navy), orange ring, minute/hour ticks,
 * a dual numeral ring (bold orange 12-hour numbers with a smaller
 * muted 24-hour number under each one), the hour/minute hands and the
 * center cap. The result is meant to be set on an ImageView inside the
 * widget's RemoteViews via setImageViewBitmap, since a home screen
 * widget cannot host a live custom View.
 */
object ClockRenderer {

    private const val BACKGROUND_COLOR = "#0C121D"
    private const val ACCENT_COLOR = "#FF8C00"
    private const val MINUTE_HAND_COLOR = "#F4E9DA"
    private const val MILITARY_NUMERAL_COLOR = "#B8AFA3"

    fun renderClockBitmap(
        context: Context,
        size: Int,
        appWidgetId: Int,
        calendar: Calendar = Calendar.getInstance()
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        drawBackground(context, canvas, size, appWidgetId)
        drawOrangeRing(canvas, size)
        drawTicksAndNumerals(canvas, size)
        drawHands(canvas, size, calendar)
        drawCenter(canvas, size)

        return bitmap
    }

    private fun drawBackground(context: Context, canvas: Canvas, size: Int, appWidgetId: Int) {
        val cx = size / 2f
        val cy = size / 2f
        val radius = size / 2f

        canvas.save()
        canvas.clipPath(Path().apply { addCircle(cx, cy, radius, Path.Direction.CW) })

        val photo = PhotoManager.loadPhoto(context, appWidgetId)

        if (photo != null) {
            val cropped = scaleCenterCrop(photo, size, size)
            canvas.drawBitmap(cropped, 0f, 0f, null)

            // Dim the photo a little so the orange hands/numerals stay readable.
            val overlay = Paint(Paint.ANTI_ALIAS_FLAG)
            overlay.color = Color.argb(130, 12, 18, 29)
            canvas.drawRect(0f, 0f, size.toFloat(), size.toFloat(), overlay)
        } else {
            canvas.drawColor(Color.parseColor(BACKGROUND_COLOR))
        }

        canvas.restore()
    }

    private fun drawOrangeRing(canvas: Canvas, size: Int) {
        val radius = size / 2f

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = size * 0.035f
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = Color.parseColor(ACCENT_COLOR)

        canvas.drawCircle(radius, radius, radius - paint.strokeWidth, paint)
    }

    private fun drawTicksAndNumerals(canvas: Canvas, size: Int) {
        val cx = size / 2f
        val cy = size / 2f
        val radius = size / 2f

        val tickOuter = radius * 0.90f
        val minuteTickInner = radius * 0.86f
        val hourTickInner = radius * 0.78f

        val minuteTickPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        minuteTickPaint.color = Color.argb(140, 244, 233, 218)
        minuteTickPaint.strokeWidth = size * 0.006f

        val hourTickPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        hourTickPaint.color = Color.parseColor(ACCENT_COLOR)
        hourTickPaint.strokeWidth = size * 0.012f
        hourTickPaint.strokeCap = Paint.Cap.ROUND

        for (i in 0 until 60) {
            val angle = i * 6.0
            val (ox, oy) = pointOnCircle(cx, cy, tickOuter, angle)
            if (i % 5 == 0) {
                val (ix, iy) = pointOnCircle(cx, cy, hourTickInner, angle)
                canvas.drawLine(ox, oy, ix, iy, hourTickPaint)
            } else {
                val (ix, iy) = pointOnCircle(cx, cy, minuteTickInner, angle)
                canvas.drawLine(ox, oy, ix, iy, minuteTickPaint)
            }
        }

        val hourNumeralPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        hourNumeralPaint.color = Color.parseColor(ACCENT_COLOR)
        hourNumeralPaint.textSize = size * 0.09f
        hourNumeralPaint.textAlign = Paint.Align.CENTER
        hourNumeralPaint.typeface = Typeface.DEFAULT_BOLD

        val militaryNumeralPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        militaryNumeralPaint.color = Color.parseColor(MILITARY_NUMERAL_COLOR)
        militaryNumeralPaint.textSize = size * 0.05f
        militaryNumeralPaint.textAlign = Paint.Align.CENTER
        militaryNumeralPaint.typeface = Typeface.DEFAULT

        val hourNumeralRadius = radius * 0.68f
        val militaryNumeralRadius = radius * 0.48f

        for (hour in 1..12) {
            val angle = hour * 30.0

            val (hx, hy) = pointOnCircle(cx, cy, hourNumeralRadius, angle)
            drawCenteredText(canvas, hour.toString(), hx, hy, hourNumeralPaint)

            val militaryHour = if (hour == 12) 0 else hour + 12
            val (mx, my) = pointOnCircle(cx, cy, militaryNumeralRadius, angle)
            drawCenteredText(canvas, militaryHour.toString().padStart(2, '0'), mx, my, militaryNumeralPaint)
        }
    }

    private fun drawHands(canvas: Canvas, size: Int, calendar: Calendar) {
        val cx = size / 2f
        val cy = size / 2f
        val radius = size / 2f

        val hour = calendar.get(Calendar.HOUR) // 0-11
        val minute = calendar.get(Calendar.MINUTE)

        val minuteAngle = minute * 6f
        val hourAngle = hour * 30f + minute * 0.5f

        val hourHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        hourHandPaint.color = Color.parseColor(ACCENT_COLOR)
        hourHandPaint.strokeWidth = size * 0.026f
        hourHandPaint.strokeCap = Paint.Cap.ROUND

        val minuteHandPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        minuteHandPaint.color = Color.parseColor(MINUTE_HAND_COLOR)
        minuteHandPaint.strokeWidth = size * 0.016f
        minuteHandPaint.strokeCap = Paint.Cap.ROUND

        canvas.save()
        canvas.rotate(hourAngle, cx, cy)
        canvas.drawLine(cx, cy + radius * 0.08f, cx, cy - radius * 0.46f, hourHandPaint)
        canvas.restore()

        canvas.save()
        canvas.rotate(minuteAngle, cx, cy)
        canvas.drawLine(cx, cy + radius * 0.10f, cx, cy - radius * 0.70f, minuteHandPaint)
        canvas.restore()
    }

    private fun drawCenter(canvas: Canvas, size: Int) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.parseColor(ACCENT_COLOR)
        canvas.drawCircle(size / 2f, size / 2f, size * 0.03f, paint)
    }

    /** Point on a circle, [angleDegFromTop] measured clockwise starting at 12 o'clock. */
    private fun pointOnCircle(cx: Float, cy: Float, radius: Float, angleDegFromTop: Double): Pair<Float, Float> {
        val rad = Math.toRadians(angleDegFromTop - 90.0)
        val x = cx + (radius * cos(rad)).toFloat()
        val y = cy + (radius * sin(rad)).toFloat()
        return x to y
    }

    private fun drawCenteredText(canvas: Canvas, text: String, x: Float, y: Float, paint: Paint) {
        val fm = paint.fontMetrics
        val baselineY = y - (fm.ascent + fm.descent) / 2f
        canvas.drawText(text, x, baselineY, paint)
    }

    /** Mimics ImageView's centerCrop: scale to cover the target box, then crop the center. */
    private fun scaleCenterCrop(source: Bitmap, targetW: Int, targetH: Int): Bitmap {
        val scale = max(targetW.toFloat() / source.width, targetH.toFloat() / source.height)
        val scaledW = (source.width * scale).toInt().coerceAtLeast(1)
        val scaledH = (source.height * scale).toInt().coerceAtLeast(1)
        val scaled = Bitmap.createScaledBitmap(source, scaledW, scaledH, true)

        val left = ((scaledW - targetW) / 2).coerceIn(0, max(scaledW - targetW, 0))
        val top = ((scaledH - targetH) / 2).coerceIn(0, max(scaledH - targetH, 0))
        val w = targetW.coerceAtMost(scaledW)
        val h = targetH.coerceAtMost(scaledH)

        return Bitmap.createBitmap(scaled, left, top, w, h)
    }
}
