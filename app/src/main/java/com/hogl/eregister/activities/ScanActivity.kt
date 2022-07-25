package com.hogl.eregister.activities

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.zxing.Result
import com.hogl.eregister.R
import com.hogl.eregister.activities.HomeActivity.Companion.RESULT
import com.hogl.eregister.data.InitApplication
import com.hogl.eregister.data.models.VisitorViewModel
import com.hogl.eregister.data.models.VisitorViewModelFactory
import me.dm7.barcodescanner.zxing.ZXingScannerView

class ScanActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    var scannerView: ZXingScannerView? = null
    private val visitorViewModel: VisitorViewModel by viewModels {
        VisitorViewModelFactory((this.application as InitApplication).visitorRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scannerView = ZXingScannerView(this)
        setContentView(scannerView)

//        TODO set this to string value
        this.title = "Scan a QR code"
        setPermission()
    }

    override fun handleResult(p0: Result?) {
        visitorViewModel.getVisitorById(p0?.toString()!!).observe(this) {
            if (it != null) {
                Toast.makeText(this, "visitor found: ${it.vis_first_name}", Toast.LENGTH_LONG)
                    .show()

                val intent = Intent(this, MovementRecordActivity::class.java)
                intent.putExtra("VISITOR_ID", it.vis_id)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No visitor found", Toast.LENGTH_LONG).show()
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