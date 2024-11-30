package com.example.appmedica.utils

data class Reminder(
    val requestCode: Int,
    val idcons: String,
    val clinica: String,
    val fecha: String,
    val hora: String,
    val calendarTime: Long,
    val notificationTitle: String?,
    val notificationText: String?,
    val actualReminder: String?
)
