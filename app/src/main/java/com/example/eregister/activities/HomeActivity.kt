package com.example.eregister.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView
import com.example.eregister.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val btnRegistered: CardView = findViewById(R.id.crdRegistered)
        val btnrNewVisitor:CardView = findViewById(R.id.crdNewVisitor)

        btnRegistered.setOnClickListener {
            val intent = Intent(this, RegisteredVisitorActivity::class.java)
            startActivity(intent)
        }

        btnrNewVisitor.setOnClickListener {
            val intent = Intent(this, NewVisitorActivity::class.java)
            startActivity(intent)
        }

        }
}