package com.example.clockwidget

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock

/** Fired by [WidgetAlarmScheduler]'s repeating alarm; redraws every widget instance. */
class ClockTickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        DarkOrangeClockWidget.updateAllWidgets(context)
    }
}

/**
 * Schedules a repeating alarm so the analog hands stay accurate to the
 * minute. AppWidgetProviderInfo.updatePeriodMillis cannot go below 30
 * minutes, which is far too coarse for a moving clock face, so we drive
 * updates ourselves instead. The interval is inexact - Android may batch
 * or delay it a little for battery reasons - but it self-corrects every
 * tick since each redraw reads the real system time.
 */
object WidgetAlarmScheduler {

    private const val ACTION_TICK = "com.example.clockwidget.ACTION_TICK"
    private const val REQUEST_CODE = 1001
    private const val TICK_INTERVAL_MILLIS = 60_000L

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ClockTickReceiver::class.java).apply {
            action = ACTION_TICK
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime(),
            TICK_INTERVAL_MILLIS,
            pendingIntent(context)
        )
    }

    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent(context))
    }
}
