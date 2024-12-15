package com.example.appmedica.com.example.appmedica

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.example.appmedica.DatabaseHandler
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

            // Definir los 3 minutos en milisegundos (3 minutos * 60 segundos * 1000 milisegundos)
            val threeMinutesInMillis = 3 * 60 * 1000

            if (calendar.before(now)) {
                if (diffInMillis > threeMinutesInMillis) {
                    // Si la fecha ya pasó hace más de 3 minutos, simplemente no hace nada
                    Log.d("AlarmUtils", "La fecha ya pasó hace más de 5 minutos. Se debería programar el siguiente")
                    programNextMedicNotification(
                        context = context,
                        title = titleNotification,
                        text = textNotification,
                        frecuencia = frecuencia,
                        duracion = duracion,
                        tipo = tipo,
                        requestCodeBase = requestCode,
                        calendar
                    )
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
                Log.d("AlarmUtils", "Recordatorio $titleNotification $textNotification creado")
            }

            // Guardar el recordatorio
            val reminder = MedReminder(
                requestCode = requestCode,
                title = titleNotification,
                text = textNotification,
                tipo = tipo,
                frecuencia = frecuencia,
                duracion = duracion,
                sigalarma = calendar,
            )
            Log.d("REMINDER_CODE",requestCode.toString())
            saveMedReminder(context, reminder) // Aquí guardamos el recordatorio
        } else{
            //El tratamiento termino
            Log.d("AlarmUtils", "El tratamiento terminó")
            return
        }

    }


    fun programNextMedicNotification(
        context: Context,
        title: String,
        text: String,
        frecuencia: String,
        duracion: String,
        tipo: String,
        requestCodeBase: Int,
        calendar: Calendar
    ) {
        val databaseHandler = DatabaseHandler(context)
        val usuario = databaseHandler.consultaDatos()
        //val calendar = Calendar.getInstance()
        val now = Calendar.getInstance()
        when {
            // Si la frecuencia es un número
            frecuencia.toIntOrNull() != null -> {
                val horas = frecuencia.toInt()
                calendar.add(Calendar.HOUR_OF_DAY, horas) // Sumamos las horas a la hora actual
            }

            // Si la frecuencia es "con cada comida"
            frecuencia == "Con cada comida" -> {
                val comidas = listOf(usuario.h3, usuario.h4, usuario.h5) // Horas de desayuno, comida y cena
                var calendarlisto = false
                for (comida in comidas) {
                    val proximaHora = Utilidades.convertirHora(comida!!)
                    Log.i("AlarmUtils", "Sig hora de comida $proximaHora")
                    val proximaHoraCalendar = Utilidades.stringToCalendar(proximaHora)
                    if (proximaHoraCalendar.after(now)) {
                        calendar.time = proximaHoraCalendar.time
                        calendarlisto = true
                    }
                }
                // Si ya pasaron todas las comidas del día, tomar el desayuno del día siguiente
                if(!calendarlisto){
                    val proximaHora = Utilidades.convertirHora(usuario.h3!!)
                    val proximaHoraCalendar = Utilidades.stringToCalendar(proximaHora)
                    proximaHoraCalendar.add(Calendar.DAY_OF_YEAR, 1)
                    calendar.time = proximaHoraCalendar.time
                }
            }

            // Si la frecuencia es "con el desayuno"
            frecuencia == "Con el desayuno" -> {
                val desayunoS = Utilidades.convertirHora(usuario.h3!!)
                val desayuno = Utilidades.stringToCalendar(desayunoS)
                if (desayuno.after(now)) {
                    calendar.time = desayuno.time
                } else {
                    desayuno.add(Calendar.DAY_OF_YEAR, 1) // Tomar el desayuno del día siguiente
                    calendar.time = desayuno.time
                }
            }

            // Si la frecuencia es "con la comida"
            frecuencia == "Con la comida" -> {
                val comidaS = Utilidades.convertirHora(usuario.h4!!)
                val comida = Utilidades.stringToCalendar(comidaS)
                if (comida.after(now)) {
                    calendar.time = comida.time
                } else {
                    comida.add(Calendar.DAY_OF_YEAR, 1) // Tomar la comida del día siguiente
                    calendar.time = comida.time
                }
            }

            // Si la frecuencia es "con la cena"
            frecuencia == "Con la cena" -> {
                val cenaS = Utilidades.convertirHora(usuario.h5!!)
                val cena = Utilidades.stringToCalendar(cenaS)
                if (cena.after(now)) {
                    calendar.time = cena.time
                } else {
                    cena.add(Calendar.DAY_OF_YEAR, 1) // Tomar la cena del día siguiente
                    calendar.time = cena.time
                }
            }

            // Si la frecuencia es "al despertar"
            frecuencia == "Al despertar" -> {
                val despertarS = Utilidades.convertirHora(usuario.h4!!) // Suponemos que h4 es la hora de despertar
                val despertar = Utilidades.stringToCalendar(despertarS)
                if (despertar.after(now)) {
                    calendar.time = despertar.time
                } else {
                    despertar.add(Calendar.DAY_OF_YEAR, 1) // Tomar el despertar del día siguiente
                    calendar.time = despertar.time
                }
            }

            // Si la frecuencia es "antes de dormir"
            frecuencia == "Antes de dormir" -> {
                val dormirS = Utilidades.convertirHora(usuario.h5!!) // Suponemos que h5 es la hora antes de dormir
                val dormir = Utilidades.stringToCalendar(dormirS)
                if (dormir.after(now)) {
                    calendar.time = dormir.time
                } else {
                    dormir.add(Calendar.DAY_OF_YEAR, 1) // Tomar antes de dormir del día siguiente
                    calendar.time = dormir.time
                }
            }

            // Caso por defecto
            else -> throw IllegalArgumentException("Frecuencia no válida: $frecuencia")
        }
        Log.i("MedicineNotification", "Nuevo recordatorio $title programado $calendar")
        scheduleNotificationMedic(context, calendar, title, text, requestCodeBase, frecuencia, duracion, tipo)
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
        val type = object : TypeToken<List<MedReminder>>() {}.type

        return gson.fromJson(json, type) ?: emptyList()
    }

    //Función que obtiene la siguiente toma
    fun getMedReminderByRequestCode(context: Context, requestCode: Int): String {
        val reminders = getMedReminders(context)
        val reminder = reminders.find { it.requestCode == requestCode }

        return if (reminder != null) {
            Log.i("AlarmUtils", "Encontró el reminder solicitado")
            val nextAlarm = reminder.sigalarma
            val year = nextAlarm.get(Calendar.YEAR)
            val month = nextAlarm.get(Calendar.MONTH) + 1 // Meses empiezan en 0
            val day = nextAlarm.get(Calendar.DAY_OF_MONTH)
            val hour = nextAlarm.get(Calendar.HOUR_OF_DAY)
            val minute = nextAlarm.get(Calendar.MINUTE)

            "Siguiente toma:\n%04d-%02d-%02d %02d:%02d".format(year, month, day, hour, minute)
        } else {
            "Sin información"
        }
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
        cancelMedAlarm(context, requestCode)
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
        val remindersList = getMedReminders(context).toMutableList()

        // Filtrar los recordatorios eliminando el que coincida con el requestCode
        val updatedList = remindersList.filter { it.requestCode != requestCode }

        // Guardar la lista actualizada en SharedPreferences
        val editor = sharedPreferences.edit()
        editor.putString("medreminders", gson.toJson(updatedList))
        editor.apply()
        Log.d("MED_REMINDERS", gson.toJson(updatedList))
    }

}
