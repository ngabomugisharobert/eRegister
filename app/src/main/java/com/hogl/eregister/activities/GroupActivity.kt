package com.hogl.eregister.activities

import android.content.ClipData.newIntent
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.hogl.eregister.R
import com.hogl.eregister.databinding.ActivityGroupBinding
import com.hogl.eregister.databinding.ActivityHomeBinding

class GroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupBinding

    private lateinit var btnScanQrCodes : Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()

        onClickListerners()



    }

    private fun onClickListerners() {
        btnScanQrCodes.setOnClickListener {
            val intent1 = Intent(applicationContext, ScanActivity::class.java)
            startActivity(intent1)
        }
    }

    private fun initComponents() {
        this.title = "Group"

        btnScanQrCodes = binding.btnScanQrCodes
    }
}