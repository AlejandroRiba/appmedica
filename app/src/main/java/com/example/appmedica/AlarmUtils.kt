package com.example.appmedica.com.example.appmedica

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

object AlarmUtils {
    private val pendingIntentMap = mutableMapOf<Int, PendingIntent>()

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification(context: Context, calendar: Calendar, notificationCont: Pair<Pair<String, String>, String>?, requestCode: Int, idcons: String, clinica: String, hora: String, fecha: String) {
        val intent = Intent(context, AlarmNotification::class.java).apply {
            putExtra("notification_title", notificationCont?.first?.first)
            putExtra("notification_text", notificationCont?.first?.second)
            putExtra("actualReminder", notificationCont?.second)
            putExtra("idcons", idcons)
            putExtra("clinica", clinica)
            putExtra("fecha", fecha)
            putExtra("hora", hora)
            putExtra("requestCode", requestCode)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Almacenar el PendingIntent con su ID (requestCode)
        pendingIntentMap[requestCode] = pendingIntent
        Log.d("intent", idcons)
        if (notificationCont != null) {
            Log.d("actualreminder", notificationCont.second)
        } //Mensaje para monitorear


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
        for ((_, pendingIntent) in pendingIntentMap) {
            alarmManager.cancel(pendingIntent)
        }
        pendingIntentMap.clear() // Limpiar todo el mapa
    }

    fun cancelAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Buscar el PendingIntent asociado al requestCode
        val pendingIntent = pendingIntentMap[requestCode]
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntentMap.remove(requestCode) // Eliminar el recordatorio del mapa
        }
    }
}
