package com.hogl.eregister.utils

import android.content.Context
import android.content.SharedPreferences
import com.hogl.eregister.User


class SessionManagement(context: Context) {
    var sharedPreferences: SharedPreferences
    var editor: SharedPreferences.Editor
    var SHARED_PREF_NAME = "session"
    var SESSION_KEY = "session_user"

    fun saveSession(user: User) {
        //save session of user whenever user is logged in
        val gua_id: Int = user.gua_id
        editor.putInt(SESSION_KEY, gua_id).commit()
    }

    //return user id whose session is saved
    val session: Int
        get() =//return user id whose session is saved
            sharedPreferences.getInt(SESSION_KEY, -1)

    fun removeSession() {
        editor.putInt(SESSION_KEY, -1).commit()
    }

    init {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }
}