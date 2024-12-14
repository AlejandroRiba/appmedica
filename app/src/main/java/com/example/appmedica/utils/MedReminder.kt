package com.example.appmedica.utils

import java.util.Calendar

data class MedReminder (
    val requestCode: Int,
    val title: String,
    val text: String,
    val tipo: String,
    val frecuencia: String,
    val duracion: String,
    val sigalarma: Calendar
)