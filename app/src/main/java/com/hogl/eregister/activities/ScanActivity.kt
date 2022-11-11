package com.hogl.eregister.activities

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import com.hogl.eregister.R
import com.hogl.eregister.activities.HomeActivity.Companion.RESULT
import com.hogl.eregister.data.InitApplication
import com.hogl.eregister.data.entities.GroupMovement
import com.hogl.eregister.data.models.GroupMovementViewModel
import com.hogl.eregister.data.models.GroupMovementViewModelFactory
import com.hogl.eregister.data.models.VisitorViewModel
import com.hogl.eregister.data.models.VisitorViewModelFactory
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var sharedPreferencesCheckIn: SharedPreferences

    private lateinit var grp_id: String
    private lateinit var action: String

    var scannerView: ZXingScannerView? = null
    private val visitorViewModel: VisitorViewModel by viewModels {
        VisitorViewModelFactory((this.application as InitApplication).visitorRepository)
    }
    private val groupMovementViewModel: GroupMovementViewModel by viewModels {
        GroupMovementViewModelFactory((this.application as InitApplication).groupMovementRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
        setContentView(scannerView)
        initScan()

//        TODO set this to string value
        this.title = "Scan a QR code"
        setPermission()
    }

    private fun initScan() {


        sharedPreferencesCheckIn = getSharedPreferences("CHECK_IN", 0)

        //get extra from intent
        val intent = intent
        action = intent.getStringExtra("ACTION").toString()
        if (action == "GROUP_CHECK_IN") {
            grp_id = intent.getStringExtra("GROUP_ID").toString()
        } else if (action == "GROUP_CHECK_OUT") {
            grp_id = intent.getStringExtra("GROUP_ID").toString()
        }
    }

    override fun handleResult(p0: Result?) {
        if (action != "GROUP_CHECK_IN" && action != "GROUP_CHECK_OUT") {
            visitorViewModel.getVisitorById(p0.toString()).observe(this) {
                if (it != null) {
                    Toast.makeText(this.applicationContext, "visitor found", Toast.LENGTH_LONG)
                        .show()
                    val intent = Intent(this, MovementRecordActivity::class.java)
                    intent.putExtra("VISITOR_ID", it.vis_id)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No visitor found", Toast.LENGTH_LONG).show()
                }
            }
            scannerView?.resumeCameraPreview(this)
        }
        else{
            //check if the result is in preferences
            if (action == "GROUP_CHECK_IN") {
                if (sharedPreferencesCheckIn.contains(p0.toString())) {
                    Toast.makeText(this, "Already checked in with this QR code", Toast.LENGTH_LONG).show()
                    scannerView?.resumeCameraPreview(this)
                } else {
                    sharedPreferencesCheckIn.edit().putString(p0.toString(), p0.toString()).apply()
                    var mv_in = groupMovementViewModel.insert(GroupMovement(0,grp_id.toInt(),p0.toString(),"CHECK_IN",System.currentTimeMillis()))
                    Log.d("CHECK_IN",mv_in.toString())
                    Toast.makeText(this, "Checked in", Toast.LENGTH_LONG).show()
                    finish()
                }
            } else if (action == "GROUP_CHECK_OUT") {
                if (!sharedPreferencesCheckIn.contains(p0.toString())) {
                    Toast.makeText(this, "Already checked - out", Toast.LENGTH_LONG)
                        .show()
                    scannerView?.resumeCameraPreview(this)
                } else {
                    sharedPreferencesCheckIn.edit().remove(p0.toString()).apply()
                    groupMovementViewModel.insert(GroupMovement(0,grp_id.toInt(),p0.toString(),"CHECK_OUT",System.currentTimeMillis()))
                    Toast.makeText(this, "Checked - out", Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        scannerView?.setResultHandler(this)
        scannerView?.startCamera()
    }

    override fun onStop() {
        super.onStop()
        scannerView?.stopCamera()
        onBackPressed()
    }

    private fun setPermission() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA),
            1
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(
                        applicationContext,
                        "You need camera permission",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}