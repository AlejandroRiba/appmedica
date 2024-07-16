package com.example.appmedica.com.example.appmedica

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        //showNotification(message)
    }
}