package com.example.appmedica.com.example.appmedica

import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.appmedica.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        //showNotification(message)
    }

}