package com.example.appmedica.com.example.appmedica

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.example.appmedica.MedicineNotification
import com.example.appmedica.utils.MedReminder
import com.example.appmedica.utils.Reminder
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.util.Calendar

object AlarmUtils {
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
                val intentperms = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intentperms)
                Log.d("AlarmUtils", "scheduleNotification: ")
            }
        }

        //alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
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

    fun scheduleNotificationMedic( context: Context, calendar: Calendar, titleNotification: String, textNotification: String, requestCode: Int, frecuencia: String, duracion: String, tipo: String) {
        val intent = Intent(context, MedicineNotification::class.java).apply {
            putExtra("notification_title", titleNotification)
            putExtra("notification_text", textNotification)
            putExtra("tipo", tipo)
            putExtra("frecuencia", frecuencia)
            putExtra("duracion", duracion)
            putExtra("requestCode", requestCode)
        }

        val now = Calendar.getInstance()
        var programa = true
        if(duracion == "Tratamiento continuo"){
            programa = true
        }else{
            val duracionCalendar = Utilidades.stringToCalendar(duracion)
            if(duracionCalendar.before(now)){
                programa = false
            }
        }

        if(programa){
            Log.d("AlarmUtil", "El tratamiento continua")
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            val calendarTime = calendar.timeInMillis // Obtener el valor en milisegundos de la fecha de calendar (recordatorio a programar)
            val nowTime = now.timeInMillis // Obtener el valor en milisegundos del momento actual

            // Calcular la diferencia en milisegundos
            val diffInMillis = nowTime - calendarTime

            // Definir los 3 minutos en milisegundos (5 minutos * 60 segundos * 1000 milisegundos)
            val threeMinutesInMillis = 3 * 60 * 1000

            if (calendar.before(now)) {
                if (diffInMillis > threeMinutesInMillis) {
                    // Si la fecha ya pasó hace más de 3 minutos, simplemente no hace nada
                    Log.d("AlarmUtils", "La fecha ya pasó hace más de 5 minutos. Se debería programar el siguiente")
                    return
                } else {
                    // Si la fecha está en el rango de los últimos 3 minutos, notificar
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (!alarmManager.canScheduleExactAlarms()) {
                            // Redirige al usuario a la configuración para habilitarlo
                            val intentperms = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                            context.startActivity(intentperms)
                            Log.d("AlarmUtils", "scheduleNotification: ")
                        }
                    }

                    //alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                    val notifAhora = Calendar.getInstance().apply {
                        add(Calendar.MILLISECOND, 3000) //
                    }
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, notifAhora.timeInMillis, pendingIntent)
                    Log.d("AlarmUtils", "Recordatorio $textNotification creado")
                }
            } else {
                // Si la fecha de calendar aún no ha pasado
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (!alarmManager.canScheduleExactAlarms()) {
                        // Redirige al usuario a la configuración para habilitarlo
                        val intentperms = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        context.startActivity(intentperms)
                        Log.d("AlarmUtils", "scheduleNotification: ")
                    }
                }

                //alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                Log.d("AlarmUtils", "Recordatorio $textNotification creado")
            }

            // Guardar el recordatorio
            val reminder = MedReminder(
                requestCode = requestCode,
                title = titleNotification,
                text = textNotification,
                tipo = tipo,
                frecuencia = frecuencia,
                duracion = duracion
            )
            Log.d("REMINDER_CODE",requestCode.toString())
            saveMedReminder(context, reminder) // Aquí guardamos el recordatorio
        } else{
            //El tratamiento termino
            Log.d("AlarmUtils", "El tratamiento terminó")
            return
        }

    }


    //Función para guardar un recordatorio de citas
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

    //Función para guardar un recordatorio de medicamento
    fun saveMedReminder(context: Context, reminder: MedReminder) {
        val sharedPreferences = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()

        // Recuperar la lista actual de recordatorios y agregar el nuevo
        val remindersList = getMedReminders(context).toMutableList()
        remindersList.add(reminder)

        // Guardar la lista actualizada
        editor.putString("medreminders", gson.toJson(remindersList))
        editor.apply()
        Log.d("MED_REMINDERS_SAVE",gson.toJson(remindersList))
    }


    //Función para obtener un recordatorio de citas
    fun getReminders(context: Context): List<Reminder> {
        val sharedPreferences = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("reminders", null)
        val type = object : TypeToken<List<Reminder>>() {}.type

        return gson.fromJson(json, type) ?: emptyList()
    }

    //Función para obtener los recordatorios de medicamentos
    fun getMedReminders(context: Context): List<MedReminder> {
        val sharedPreferences = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("medreminders", null)
        val type = object : TypeToken<List<Reminder>>() {}.type

        return gson.fromJson(json, type) ?: emptyList()
    }

    //Función para eliminar un recordatorio de cita
    fun deleteReminder(context: Context, requestCode: Int) {
        // Cancelar la alarma
        cancelAlarm(context, requestCode)
        Log.d("CANCEL ALARM", requestCode.toString())

        // Eliminar el recordatorio de SharedPreferences
        removeReminder(context, requestCode)
    }

    //Función para eliminar un recordatorio de medicamento
    fun deleteMedReminder(context: Context, requestCode: Int) {
        // Cancelar la alarma
        cancelAlarm(context, requestCode)
        Log.d("CANCEL ALARM", requestCode.toString())

        // Eliminar el recordatorio de SharedPreferences
        removeMedReminder(context, requestCode)
    }

    //Función para cancelar todos los recordatorios de citas
    fun cancelAllAlarms(context: Context) {
        // Obtener la lista actual de recordatorios
        val remindersList = getReminders(context)

        // Cancelar cada recordatorio
        remindersList.forEach { reminder ->
            deleteReminder(context, reminder.requestCode)
        }
    }

    //Función para cancelar todos los recordatorios de medicamentos
    fun cancelAllMedAlarms(context: Context) {
        // Obtener la lista actual de recordatorios
        val remindersList = getMedReminders(context)

        // Cancelar cada recordatorio
        remindersList.forEach { reminder ->
            deleteMedReminder(context, reminder.requestCode)
        }
    }

    //Función para cancelar alarm de un recordatorio de citas
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

    //Función para cancelar la alarma de medicamentos
    fun cancelMedAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Crear un Intent equivalente al que se usó para programar la alarma
        val intent = Intent(context, MedicineNotification::class.java)
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

    //Función para remover un recordatorio de CITAS
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

    //Función para remover un recordatorio de medicamento
    fun removeMedReminder(context: Context, requestCode: Int) {
        val sharedPreferences = context.getSharedPreferences("ReminderPrefs", Context.MODE_PRIVATE)
        val gson = Gson()

        // Obtener la lista actual de recordatorios
        val remindersList = getReminders(context).toMutableList()

        // Filtrar los recordatorios eliminando el que coincida con el requestCode
        val updatedList = remindersList.filter { it.requestCode != requestCode }

        // Guardar la lista actualizada en SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("medreminders", gson.toJson(updatedList))
        editor.apply()
        Log.d("MED_REMINDERS", gson.toJson(updatedList))
    }

}
