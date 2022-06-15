package com.example.eregister

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View
import com.example.eregister.activities.HomeActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



    }
    fun fnLogin(view: View){
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, "text")
        }
        startActivity(intent)
    }
}