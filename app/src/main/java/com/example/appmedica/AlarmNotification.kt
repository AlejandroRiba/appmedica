package com.example.appmedica.com.example.appmedica

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.appmedica.ListaConsultas
import com.example.appmedica.R

class AlarmNotification:BroadcastReceiver() {
    companion object{
        var NOTIFICATION_ID = 0
    }
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("notification_title") ?: "Título predeterminado"
        val text = intent?.getStringExtra("notification_text") ?: "Texto predeterminado"
        val actualReminder = intent?.getStringExtra("actualReminder") ?: "none"
        val idcons = intent?.getStringExtra("idcons") ?: "none"
        val clinica = intent?.getStringExtra("clinica") ?: "none"
        val fecha =  intent?.getStringExtra("fecha") ?: "none"
        val hora =  intent?.getStringExtra("hora") ?: "none"
        val requestCodeBase = intent?.getIntExtra("requestCode", 0)
        if (requestCodeBase != null) {
            Log.d("REMINDER_REQUESTCODE", requestCodeBase.toString())
            AlarmUtils.deleteReminder(context, requestCodeBase)
            programNextNotification(context, actualReminder, idcons, clinica, fecha, hora, requestCodeBase)
        }
        showNotification(context, title, text)
    }
    private fun showNotification(context: Context,  title: String, text: String) {
        val intent = Intent(context, ListaConsultas::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, flag)

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(text)  // El texto largo que quieres mostrar

        val notification = NotificationCompat.Builder(context, MyApp.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setStyle(bigTextStyle)
            .setSmallIcon(R.drawable.notification_logo)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID++, notification)
    }

    private fun programNextNotification(context: Context ,actualReminder: String, idcons: String, clinica: String, fecha: String, hora: String, requestCodeBase: Int){
        val notifantes = when (actualReminder) {
            "5mins" -> Utilidades.obtenerFechaHora(fecha, hora) // Busca la fecha para el primer recordatorio
            "2hrs" -> Utilidades.restarCincoMinutos(fecha, hora) // 2 horas antes
            "3dias" -> Utilidades.restarDia(fecha, hora) // 3 días antes
            "1dia" -> Utilidades.restarDosHoras(fecha, hora) // 1 día antes
            else -> {
                // Si no hay coincidencia, no hace nada
                null
            }
        }

        notifantes?.let {
            val mensaje = Utilidades.obtenerMensajeCita(actualReminder, idcons, clinica, hora, fecha)
            Log.d("nuevorecordatorio", mensaje.second)
            AlarmUtils.scheduleNotification(context,it, mensaje, requestCodeBase, //genera el nuevo recordatorio
                idcons, // Consulta id
                clinica, // Consulta clínica
                hora,
                fecha)
        }
    }

}