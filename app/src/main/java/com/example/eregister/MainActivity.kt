package com.example.eregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.View
import androidx.lifecycle.Lifecycle
import com.example.eregister.activities.HomeActivity
import com.example.eregister.lifecycle.MainActivityObserver

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i(TAG,"Activity onCreated")
        lifecycle.addObserver(MainActivityObserver())

    }
    fun fnLogin(view: View){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    companion object{
        private val TAG:String = MainActivityObserver::class.java.simpleName
        private const val NEW_VISITOR_ACTIVITY_REQUEST_CODE = 1
    }
}