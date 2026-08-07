package com.example.clockwidget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** AlarmManager alarms are cleared on reboot; this puts the tick alarm back and redraws immediately. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            WidgetAlarmScheduler.schedule(context)
            DarkOrangeClockWidget.updateAllWidgets(context)
        }
    }
}
