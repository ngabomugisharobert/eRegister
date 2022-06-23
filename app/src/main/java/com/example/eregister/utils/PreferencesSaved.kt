package com.example.eregister.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesSaved {

    fun savePreference(key: String, value: String, context: Context) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }
}