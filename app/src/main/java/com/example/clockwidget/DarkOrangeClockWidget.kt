package com.example.clockwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.Settings
import android.widget.RemoteViews
import java.util.Calendar

class DarkOrangeClockWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
        scheduleNextUpdate(context)
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        updateAppWidget(context, appWidgetManager, appWidgetId)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (ACTION_UPDATE_CLOCK == intent.action ||
            Intent.ACTION_TIME_CHANGED == intent.action ||
            Intent.ACTION_TIMEZONE_CHANGED == intent.action
        ) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val thisWidget = ComponentName(context, DarkOrangeClockWidget::class.java)
            val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
            for (widgetId in allWidgetIds) {
                updateAppWidget(context, appWidgetManager, widgetId)
            }
            scheduleNextUpdate(context)
        }
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        cancelNextUpdate(context)
    }

    companion object {
        const val ACTION_UPDATE_CLOCK = "com.example.clockwidget.ACTION_UPDATE_CLOCK"
        private const val DEFAULT_BITMAP_SIZE = 600
        private const val MIN_BITMAP_SIZE = 300

        private val COLOR_NAVY = Color.parseColor("#0C121D")
        private val COLOR_ORANGE = Color.parseColor("#FF9E00")
        private val COLOR_MINUTE_DOT = Color.parseColor("#4B5B6E")
        private val COLOR_SLATE_24H = Color.parseColor("#7A8A9E")
        private val COLOR_MINUTE_HAND = Color.parseColor("#B0BEC5")

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val options = appWidgetManager.getAppWidgetOptions(appWidgetId)
            val minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 180)
            val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 180)
            val maxWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 180)
            val maxHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 180)

            val targetSize = maxOf(minWidth, minHeight, maxWidth, maxHeight)
                .coerceAtLeast(MIN_BITMAP_SIZE)
                .coerceAtMost(DEFAULT_BITMAP_SIZE)

            val density = context.resources.displayMetrics.density
            val bitmapSize = (targetSize * density).toInt().coerceAtLeast(MIN_BITMAP_SIZE)

            val bitmap = renderClockBitmap(context, bitmapSize)
            val views = RemoteViews(context.packageName, R.layout.widget_24h_clock)
            views.setImageViewBitmap(R.id.clock_view, bitmap)

            createClockPendingIntent(context)?.let {
                views.setOnClickPendingIntent(R.id.clock_view, it)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun createClockPendingIntent(context: Context): PendingIntent? {
            val deskClockIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                component = ComponentName(
                    "com.google.android.deskclock",
                    "com.android.deskclock.DeskClock"
                )
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            if (context.packageManager.resolveActivity(deskClockIntent, 0) != null) {
                return PendingIntent.getActivity(
                    context, 0, deskClockIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

            val alarmIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            if (context.packageManager.resolveActivity(alarmIntent, 0) != null) {
                return PendingIntent.getActivity(
                    context, 0, alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }

            val settingsIntent = Intent(Settings.ACTION_DATE_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            return try {
                PendingIntent.getActivity(
                    context, 0, settingsIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } catch (e: Exception) {
                null
            }
        }

        fun renderClockBitmap(
            context: Context,
            size: Int
        ): Bitmap {
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val photo = PhotoManager.loadPhoto(context)
            val canvas = Canvas(bitmap)

            val cx = size / 2f
            val cy = size / 2f
            val scaleFactor = size / 600f
            val outerRadius = size / 2f - 16f * scaleFactor

            // Photo / Background rendering
            if (photo != null) {
                val scaled = Bitmap.createScaledBitmap(photo, size, size, true)
                canvas.drawBitmap(scaled, 0f, 0f, null)
                val overlay = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = Color.argb(90, 0, 0, 0)
                }
                canvas.drawCircle(cx, cy, outerRadius, overlay)
            } else {
                val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = COLOR_NAVY
                    style = Paint.Style.FILL
                }
                canvas.drawCircle(cx, cy, outerRadius, bgPaint)
            }

            val bezelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = COLOR_ORANGE
                style = Paint.Style.STROKE
                strokeWidth = 10f * scaleFactor
            }
            canvas.drawCircle(cx, cy, outerRadius - 5f * scaleFactor, bezelPaint)

            val dotRadiusTrack = outerRadius - 28f * scaleFactor
            val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
            }
            for (i in 0 until 60) {
                val angleDeg = i * 6f - 90f
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val dotX = (cx + dotRadiusTrack * Math.cos(angleRad)).toFloat()
                val dotY = (cy + dotRadiusTrack * Math.sin(angleRad)).toFloat()
                val dotSize = if (i % 5 == 0) 5.5f * scaleFactor else 2.5f * scaleFactor
                dotPaint.color = if (i % 5 == 0) COLOR_ORANGE else COLOR_MINUTE_DOT
                canvas.drawCircle(dotX, dotY, dotSize, dotPaint)
            }

            val outerNumRadius = outerRadius - 65f * scaleFactor
            val outerNumPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = COLOR_ORANGE
                textSize = 34f * scaleFactor
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
            }
            for (i in 1..12) {
                val angleDeg = i * 30f - 90f
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val numX = (cx + outerNumRadius * Math.cos(angleRad)).toFloat()
                val numY = (cy + outerNumRadius * Math.sin(angleRad)).toFloat()
                val baselineY = numY - (outerNumPaint.descent() + outerNumPaint.ascent()) / 2f
                canvas.drawText(i.toString(), numX, baselineY, outerNumPaint)
            }

            val innerNumRadius = outerRadius - 120f * scaleFactor
            val innerNumPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = COLOR_SLATE_24H
                textSize = 24f * scaleFactor
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                textAlign = Paint.Align.CENTER
            }
            for (i in 1..12) {
                val hour24Str = if (i == 12) "0" else (i + 12).toString()
                val angleDeg = i * 30f - 90f
                val angleRad = Math.toRadians(angleDeg.toDouble())
                val numX = (cx + innerNumRadius * Math.cos(angleRad)).toFloat()
                val numY = (cy + innerNumRadius * Math.sin(angleRad)).toFloat()
                val baselineY = numY - (innerNumPaint.descent() + innerNumPaint.ascent()) / 2f
                canvas.drawText(hour24Str, numX, baselineY, innerNumPaint)
            }

            val calendar = Calendar.getInstance()
            val hour12 = calendar.get(Calendar.HOUR)
            val minute = calendar.get(Calendar.MINUTE)

            val hourAngleDeg = (hour12 + minute / 60f) * 30f - 90f
            val hourAngleRad = Math.toRadians(hourAngleDeg.toDouble())
            val hourHandLength = outerRadius - 150f * scaleFactor
            val hourHandBaseWidth = 18f * scaleFactor
            val hourHandTailLength = 30f * scaleFactor

            val hourTipX = (cx + hourHandLength * Math.cos(hourAngleRad)).toFloat()
            val hourTipY = (cy + hourHandLength * Math.sin(hourAngleRad)).toFloat()
            val perpRadLeft = hourAngleRad - Math.PI / 2
            val perpRadRight = hourAngleRad + Math.PI / 2
            val hourBaseLeftX = (cx + (hourHandBaseWidth / 2f) * Math.cos(perpRadLeft)).toFloat()
            val hourBaseLeftY = (cy + (hourHandBaseWidth / 2f) * Math.sin(perpRadLeft)).toFloat()
            val hourBaseRightX = (cx + (hourHandBaseWidth / 2f) * Math.cos(perpRadRight)).toFloat()
            val hourBaseRightY = (cy + (hourHandBaseWidth / 2f) * Math.sin(perpRadRight)).toFloat()
            val hourTailX = (cx - hourHandTailLength * Math.cos(hourAngleRad)).toFloat()
            val hourTailY = (cy - hourHandTailLength * Math.sin(hourAngleRad)).toFloat()

            val hourPath = Path().apply {
                moveTo(hourTipX, hourTipY)
                lineTo(hourBaseRightX, hourBaseRightY)
                lineTo(hourTailX, hourTailY)
                lineTo(hourBaseLeftX, hourBaseLeftY)
                close()
            }
            val hourPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = COLOR_ORANGE
                style = Paint.Style.FILL
            }
            canvas.drawPath(hourPath, hourPaint)

            val minuteAngleDeg = minute * 6f - 90f
            val minuteAngleRad = Math.toRadians(minuteAngleDeg.toDouble())
            val minuteHandLength = outerRadius - 45f * scaleFactor
            val minuteTipX = (cx + minuteHandLength * Math.cos(minuteAngleRad)).toFloat()
            val minuteTipY = (cy + minuteHandLength * Math.sin(minuteAngleRad)).toFloat()
            val minuteTailLength = 35f * scaleFactor
            val minuteTailX = (cx - minuteTailLength * Math.cos(minuteAngleRad)).toFloat()
            val minuteTailY = (cy - minuteTailLength * Math.sin(minuteAngleRad)).toFloat()

            val minutePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = COLOR_MINUTE_HAND
                style = Paint.Style.STROKE
                strokeWidth = 6f * scaleFactor
                strokeCap = Paint.Cap.ROUND
            }
            canvas.drawLine(minuteTailX, minuteTailY, minuteTipX, minuteTipY, minutePaint)

            val centerPinRadius = 14f * scaleFactor
            val centerPinBorderWidth = 4f * scaleFactor
            val centerPinBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = COLOR_NAVY
                style = Paint.Style.FILL
            }
            canvas.drawCircle(cx, cy, centerPinRadius + centerPinBorderWidth / 2f, centerPinBorderPaint)
            val centerPinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = COLOR_ORANGE
                style = Paint.Style.FILL
            }
            canvas.drawCircle(cx, cy, centerPinRadius, centerPinPaint)

            return bitmap
        }

        private fun scheduleNextUpdate(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, DarkOrangeClockWidget::class.java).apply {
                action = ACTION_UPDATE_CLOCK
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val calendar = Calendar.getInstance().apply {
                add(Calendar.MINUTE, 1)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC, calendar.timeInMillis, pendingIntent
                        )
                    } else {
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC, calendar.timeInMillis, pendingIntent
                        )
                    }
                }
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC, calendar.timeInMillis, pendingIntent
                    )
                }
                else -> {
                    alarmManager.setExact(
                        AlarmManager.RTC, calendar.timeInMillis, pendingIntent
                    )
                }
            }
        }

        private fun cancelNextUpdate(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, DarkOrangeClockWidget::class.java).apply {
                action = ACTION_UPDATE_CLOCK
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            pendingIntent?.let { alarmManager.cancel(it) }
        }
    }
}
