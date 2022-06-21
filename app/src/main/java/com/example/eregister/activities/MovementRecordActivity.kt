package com.example.eregister.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.eregister.R
import com.example.eregister.lifecycle.MainActivityObserver

class MovementRecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movement_record)
        Toast.makeText(this,"THiS SHIRT IS REAL", Toast.LENGTH_LONG).show()
    }
    companion object{
        private val TAG:String = MainActivityObserver::class.java.simpleName
        private const val NEW_VISITOR_ACTIVITY_REQUEST_CODE = 1
    }
}