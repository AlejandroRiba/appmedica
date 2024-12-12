package com.example.appmedica.utils

data class MedReminder (
    val requestCode: Int,
    val title: String,
    val text: String,
    val tipo: String,
    val frecuencia: String,
    val duracion: String
)