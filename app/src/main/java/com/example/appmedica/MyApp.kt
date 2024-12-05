package com.example.appmedica.com.example.appmedica

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging


class MyApp : Application() {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "notification_cita"
        const val NOTIFICATION_CHANNEL_ID2 = "notification_medicamento"
    }
    override fun onCreate() {
        super.onCreate()
        // Inicializa Firebase
        FirebaseApp.initializeApp(this)

        Firebase.messaging.token.addOnCompleteListener{
            if(!it.isSuccessful){
                println("El token no fue generado")
                return@addOnCompleteListener
            }
            val token = it.result
            println("El token es: $token")
        }
        createNotificationChannel()
    }
    private fun createNotificationChannel(){
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Notificaciones de Citas Pendientes",
            NotificationManager.IMPORTANCE_HIGH
        ).apply{
            description = "Estas notificaciones sirven para citas médicas."
        }

        val channel2 = NotificationChannel(
            NOTIFICATION_CHANNEL_ID2,
            "Notificaciones de Medicamentos",
            NotificationManager.IMPORTANCE_HIGH
        ).apply{
            description = "Estas notificaciones van a ser para la toma de medicamentos."
        }


        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
        notificationManager.createNotificationChannel(channel2)
    }

}