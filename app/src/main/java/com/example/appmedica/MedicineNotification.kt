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
            AlarmUtils.deleteMedReminder(context, requestCodeBase)
            val calendar = Calendar.getInstance() //Calculará el siguiente recordatorio a partir de aquí
            AlarmUtils.programNextMedicNotification(context, title, text, frecuencia, duracion, tipo, requestCodeBase, calendar)
        }
        showNotification(context, title, text)
    }

    private fun showNotification(context: Context, title: String, text: String) {
        val intent = Intent(context, ListaMedicamentos::class.java).apply {
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