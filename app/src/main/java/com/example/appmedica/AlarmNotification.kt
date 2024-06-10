package com.example.appmedica.com.example.appmedica

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
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
        showNotification(context, title, text)
    }
    private fun showNotification(context: Context,  title: String, text: String) {
        val intent = Intent(context, ListaConsultas::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val flag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, flag)

        val notification = NotificationCompat.Builder(context, MyApp.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.notification_logo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID++, notification)
    }
}