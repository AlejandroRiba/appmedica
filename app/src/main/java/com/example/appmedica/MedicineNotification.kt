package com.example.appmedica

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.appmedica.com.example.appmedica.AlarmUtils

class MedicineNotification : BroadcastReceiver() {
    companion object{
        var NOTIFICATION_ID = 0
    }
    override fun onReceive(context: Context, intent: Intent?) {
        val title = intent?.getStringExtra("notification_title") ?: "Título predeterminado"
        val text = intent?.getStringExtra("notification_text") ?: "Texto predeterminado"
        val actualReminder = intent?.getStringExtra("actualReminder") ?: "none"
        val nombremedicamento = intent?.getStringExtra("nombre_medicamento") ?: "none"
        val tipo = intent?.getStringExtra("tipo") ?: "none"
        val requestCodeBase = intent?.getIntExtra("requestCode", 0)
        if (requestCodeBase != null) {
            Log.d("REMINDER_REQUESTCODE", requestCodeBase.toString())
            AlarmUtils.deleteReminder(context, requestCodeBase)
            programNextNotification(context, actualReminder, nombremedicamento, tipo, requestCodeBase)
        }
        showNotification(context, title, text)
    }

    private fun programNextNotification(
        context: Context,
        actualReminder: String,
        nombremedicamento: String,
        tipo: String,
        requestCodeBase: Int
    ) {

    }

    private fun showNotification(context: Context, title: String, text: String) {

    }
}