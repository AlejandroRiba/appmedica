package com.example.appmedica

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.appmedica.com.example.appmedica.AlarmNotification
import com.example.appmedica.com.example.appmedica.AlarmUtils.getMedReminders
import com.example.appmedica.com.example.appmedica.AlarmUtils.getReminders
import java.util.Calendar

class BootReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Aquí reprograma tus alarmas
            val reminders = getReminders(context)
            val medreminders = getMedReminders(context)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            for (reminder in reminders) {
                val nuevoIntento = Intent(context, AlarmNotification::class.java).apply {
                    putExtra("notification_title", reminder.notificationTitle)
                    putExtra("notification_text", reminder.notificationText)
                    putExtra("actualReminder", reminder.actualReminder)
                    putExtra("idcons", reminder.idcons)
                    putExtra("clinica", reminder.clinica)
                    putExtra("fecha", reminder.fecha)
                    putExtra("hora", reminder.hora)
                    putExtra("requestCode", reminder.requestCode)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    reminder.requestCode,
                    nuevoIntento,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                // Solo reprograma alarmas cuyo tiempo no haya pasado
                if (reminder.calendarTime > System.currentTimeMillis()) {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        reminder.calendarTime,
                        pendingIntent
                    )
                }else{
                    // Calcular el tiempo 1 minuto después del reinicio
                    val calendar = Calendar.getInstance().apply {
                        add(Calendar.MILLISECOND, 3000) // Sumamos 1 minuto al tiempo actual
                    }
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            }

            for(medreminder in medreminders){
                val nuevointentoMed = Intent(context, MedicineNotification::class.java).apply {
                    putExtra("notification_title", medreminder.title)
                    putExtra("notification_text", medreminder.text)
                    putExtra("tipo", medreminder.tipo)
                    putExtra("frecuencia", medreminder.frecuencia)
                    putExtra("duracion", medreminder.duracion)
                    putExtra("requestCode", medreminder.requestCode)
                }
                val pendingIntentMed = PendingIntent.getBroadcast(
                    context,
                    medreminder.requestCode,
                    nuevointentoMed,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )

                // Solo reprograma alarmas cuyo tiempo no haya pasado
                if (medreminder.sigalarma.timeInMillis > System.currentTimeMillis()) {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        medreminder.sigalarma.timeInMillis,
                        pendingIntentMed
                    )
                }else{
                    // Calcular el tiempo 1 minuto después del reinicio
                    val calendar = Calendar.getInstance().apply {
                        add(Calendar.MILLISECOND, 3000) // Sumamos 1 minuto al tiempo actual
                    }
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntentMed
                    )
                }
            }
        }
    }
}
