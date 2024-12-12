package com.example.appmedica

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.appmedica.com.example.appmedica.AlarmNotification
import com.example.appmedica.com.example.appmedica.AlarmUtils
import com.example.appmedica.com.example.appmedica.MyApp
import com.example.appmedica.com.example.appmedica.Utilidades
import com.google.firebase.firestore.util.Util
import java.util.Calendar

class MedicineNotification : BroadcastReceiver() {
    companion object{
        var NOTIFICATION_ID = 0
    }
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("notification_title") ?: "Título predeterminado"
        val text = intent?.getStringExtra("notification_text") ?: "Texto predeterminado"
        val frecuencia = intent?.getStringExtra("frecuencia") ?: "12"
        val duracion = intent?.getStringExtra("duracion") ?: "Tratamiento continuo"
        val tipo = intent?.getStringExtra("tipo") ?: "none"
        val requestCodeBase = intent?.getIntExtra("requestCode", 0)
        if (requestCodeBase != null) {
            Log.d("REMINDER_REQUESTCODE", requestCodeBase.toString())
            AlarmUtils.deleteReminder(context, requestCodeBase)
            programNextNotification(context, title, text, frecuencia, duracion, tipo, requestCodeBase)
        }
        showNotification(context, title, text)
    }

    private fun programNextNotification(
        context: Context,
        title: String,
        text: String,
        frecuencia: String,
        duracion: String,
        tipo: String,
        requestCodeBase: Int
    ) {
        val databaseHandler = DatabaseHandler(context)
        val usuario = databaseHandler.consultaDatos()
        val calendar = Calendar.getInstance()
        val now = Calendar.getInstance()
        when {
            // Si la frecuencia es un número
            frecuencia.toIntOrNull() != null -> {
                val horas = frecuencia.toInt()
                calendar.add(Calendar.HOUR_OF_DAY, horas) // Sumamos las horas a la hora actual
            }

            // Si la frecuencia es "con cada comida"
            frecuencia == "con cada comida" -> {
                val comidas = listOf(usuario.h3, usuario.h4, usuario.h5) // Horas de desayuno, comida y cena
                for (comida in comidas) {
                    val proximaHora = Utilidades.convertirHora(comida!!)
                    val proximaHoraCalendar = Utilidades.stringToCalendar(proximaHora)
                    if (proximaHoraCalendar.after(now)) {
                        calendar.time = proximaHoraCalendar.time
                    }
                }
                // Si ya pasaron todas las comidas del día, tomar el desayuno del día siguiente
                val proximaHora = Utilidades.convertirHora(usuario.h3!!)
                val proximaHoraCalendar = Utilidades.stringToCalendar(proximaHora)
                proximaHoraCalendar.add(Calendar.DAY_OF_YEAR, 1)
                calendar.time = proximaHoraCalendar.time
            }

            // Si la frecuencia es "con el desayuno"
            frecuencia == "Con el desayuno" -> {
                val desayunoS = Utilidades.convertirHora(usuario.h1!!)
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
                val comidaS = Utilidades.convertirHora(usuario.h2!!)
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
                val cenaS = Utilidades.convertirHora(usuario.h3!!)
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
        AlarmUtils.scheduleNotificationMedic(context, calendar, title, text, requestCodeBase, frecuencia, duracion, tipo)
    }

    private fun showNotification(context: Context, title: String, text: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val flag = PendingIntent.FLAG_IMMUTABLE
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, flag)

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(text)  // El texto largo que quieres mostrar

        val notification = NotificationCompat.Builder(context, MyApp.NOTIFICATION_CHANNEL_ID2)
            .setContentTitle(title)
            .setStyle(bigTextStyle)
            .setSmallIcon(R.drawable.notification_logo)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(AlarmNotification.NOTIFICATION_ID++, notification)
    }
}