package com.example.appmedica.com.example.appmedica

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appmedica.utils.Reminder
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.util.Calendar

object AlarmUtils {
    @SuppressLint("ScheduleExactAlarm")
    fun scheduleNotification( context: Context, calendar: Calendar, notificationCont: Pair<Pair<String, String>, String>?, requestCode: Int, idcons: String, clinica: String, hora: String, fecha: String) {
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

        val now = Calendar.getInstance()
        if (calendar.before(now)) {
            // La fecha y hora ya han pasado simplemente no hace nada
            Log.d("AlarmUtils", "la fecha ya pasó")
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Redirige al usuario a la configuración para habilitarlo
                val intent: Intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
                Log.d("AlarmUtils", "scheduleNotification: ")
            }
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        Log.d("AlarmUtils", "Recordatorio ${notificationCont?.second} $idcons creado")

        // Guardar el recordatorio
        val reminder = Reminder(
            requestCode = requestCode,
            idcons = idcons,
            clinica = clinica,
            fecha = fecha,
            hora = hora,
            calendarTime = calendar.timeInMillis,
            notificationTitle = notificationCont?.first?.first,
            notificationText = notificationCont?.first?.second,
            actualReminder = notificationCont?.second
        )
        Log.d("REMINDER_CODE",requestCode.toString())
        saveReminder(context, reminder) // Aquí guardamos el recordatorio

    }


    fun saveReminder(context: Context, reminder: Reminder) {
        val sharedPreferences = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        // Recuperar la lista actual de recordatorios y agregar el nuevo
        val remindersList = getReminders(context).toMutableList()
        remindersList.add(reminder)

        // Guardar la lista actualizada
        editor.putString("reminders", gson.toJson(remindersList))
        editor.apply()
        Log.d("REMINDERS_SAVE",gson.toJson(remindersList))
    }

    fun getReminders(context: Context): List<Reminder> {
        val sharedPreferences = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("reminders", null)
        val type = object : TypeToken<List<Reminder>>() {}.type

        return gson.fromJson(json, type) ?: emptyList()
    }


    fun deleteReminder(context: Context, requestCode: Int) {
        // Cancelar la alarma
        cancelAlarm(context, requestCode)
        Log.d("CANCEL ALARM", requestCode.toString())

        // Eliminar el recordatorio de SharedPreferences
        removeReminder(context, requestCode)
    }

    fun cancelAllAlarms(context: Context) {
        // Obtener la lista actual de recordatorios
        val remindersList = getReminders(context)

        // Cancelar cada recordatorio
        remindersList.forEach { reminder ->
            deleteReminder(context, reminder.requestCode)
        }
    }


    fun cancelAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Crear un Intent equivalente al que se usó para programar la alarma
        val intent = Intent(context, AlarmNotification::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE // NO_CREATE asegura que no se cree si no existe
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent) // Cancela la alarma en el AlarmManager
            pendingIntent.cancel()             // Cancela el PendingIntent
        }
    }

    fun removeReminder(context: Context, requestCode: Int) {
        val sharedPreferences = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val gson = Gson()

        // Obtener la lista actual de recordatorios
        val remindersList = getReminders(context).toMutableList()

        // Filtrar los recordatorios eliminando el que coincida con el requestCode
        val updatedList = remindersList.filter { it.requestCode != requestCode }

        // Guardar la lista actualizada en SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("reminders", gson.toJson(updatedList))
        editor.apply()
        Log.d("REMINDERS", gson.toJson(updatedList))
    }

}
