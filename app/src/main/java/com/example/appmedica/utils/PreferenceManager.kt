package com.example.appmedica.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.UUID

class PreferenceManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)

    fun saveUniqueId(id: String) {
        sharedPreferences.edit().putString("unique_id", id).apply()
    }

    fun getUniqueId(): String? {
        return sharedPreferences.getString("unique_id", null)
    }

    fun generateAndSaveUniqueId() {
        val uniqueId = UUID.randomUUID().toString()
        saveUniqueId(uniqueId)
    }
}
