package com.example.appmedica.com.example.appmedica

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object AlarmUtils {
    private val pendingIntentList = mutableListOf<PendingIntent>()

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(context: Context, calendar: Calendar, notificationCont: Pair<String, String>, requestCode: Int) {
        val intent = Intent(context, AlarmNotification::class.java).apply {
            putExtra("notification_title", notificationCont.first)
            putExtra("notification_text", notificationCont.second)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        pendingIntentList.add(pendingIntent)

        val now = Calendar.getInstance()
        if (calendar.before(now)) {
            // La fecha y hora ya han pasado simplemente no hace nada
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    fun cancelAllAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (pendingIntent in pendingIntentList) {
            alarmManager.cancel(pendingIntent)
        }
        pendingIntentList.clear()
    }
}
