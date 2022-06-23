package com.example.eregister.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.cardview.widget.CardView
import com.example.eregister.R
import com.example.eregister.data.InitApplication
import com.example.eregister.data.entities.visitor.Visitor
import com.example.eregister.data.models.VisitorViewModel
import com.example.eregister.data.models.VisitorViewModelFactory
import com.example.eregister.utils.GenerateVisitorId

class HomeActivity : AppCompatActivity() {

        private val newVisitorActivityRequestCode = 1
        private val visitorViewModel: VisitorViewModel by viewModels {
            VisitorViewModelFactory((this.application as InitApplication).visitorRepository)
        }

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


        val btnRegistered: CardView = findViewById(R.id.crdRegistered)
        val btnNewVisitor: CardView = findViewById(R.id.crdNewVisitor)

        btnRegistered.setOnClickListener {
            val intent = Intent(this, RegisteredVisitorActivity::class.java)
            startActivity(intent)
        }

        var resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    Log.i(TAG, data?.getStringExtra(NewVisitorActivity.VIS_FIRST_NAME)+"*********")
                    val vis_first_name = data?.getStringExtra(NewVisitorActivity.VIS_FIRST_NAME).toString()
                    val vis_last_name = data?.getStringExtra(NewVisitorActivity.VIS_LAST_NAME).toString()
                    val vis_type = data?.getStringExtra(NewVisitorActivity.VIS_TYPE).toString()
                    val vis_phone = data?.getStringExtra(NewVisitorActivity.VIS_PHONE).toString()

                    val visitor = Visitor(GenerateVisitorId.getId(),vis_first_name,vis_last_name,5,vis_type,54)


                    visitorViewModel.insert(visitor)


                    Toast.makeText(applicationContext,"saved successfully", Toast.LENGTH_LONG).show()
                }else
                {
                    Toast.makeText(applicationContext," visitor not saved successfully", Toast.LENGTH_LONG).show()

                }
            }

        btnNewVisitor.setOnClickListener {
            val intent = Intent(this, NewVisitorActivity::class.java)
            resultLauncher.launch(intent)
        }


    }


    companion object {
        private val TAG: String = HomeActivity::class.java.simpleName
        private const val NEW_VISITOR_ACTIVITY_REQUEST_CODE = 1
    }
}

