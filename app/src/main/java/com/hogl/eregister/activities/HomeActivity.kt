package com.hogl.eregister.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.hogl.eregister.R
import com.hogl.eregister.User
import com.hogl.eregister.data.InitApplication
import com.hogl.eregister.data.entities.visitor.Visitor
import com.hogl.eregister.data.models.VisitorViewModel
import com.hogl.eregister.data.models.VisitorViewModelFactory
import com.hogl.eregister.utils.GenerateVisitorId
import com.google.gson.Gson


class HomeActivity : AppCompatActivity() {

    private val newVisitorActivityRequestCode = 1
    private val visitorViewModel: VisitorViewModel by viewModels {
        VisitorViewModelFactory((this.application as InitApplication).visitorRepository)
    }

    private lateinit var user: User
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var txtWelcome: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        txtWelcome = findViewById(R.id.txt_welcome)
        sharedPreferences =
            applicationContext.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        var gson: Gson = Gson()
        var data: String? = sharedPreferences.getString("USER", null)
        if (data != null) {
            user = gson.fromJson(data, User::class.java)
            txtWelcome.setText("Welcome, ${user.gua_fisrtname}")
        }


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
                    val vis_first_name =
                        data?.getStringExtra(NewVisitorActivity.VIS_FIRST_NAME).toString()
                    val vis_last_name =
                        data?.getStringExtra(NewVisitorActivity.VIS_LAST_NAME).toString()
                    val vis_type = data?.getStringExtra(NewVisitorActivity.VIS_TYPE).toString()
                    val vis_phone = data?.getStringExtra(NewVisitorActivity.VIS_PHONE).toString()
                    val vis_idNumber = data?.getStringExtra(NewVisitorActivity.VIS_ID_NUMBER).toString()
                    val visitor = Visitor(
                        GenerateVisitorId.getId(),
                        vis_first_name,
                        vis_last_name,
                        vis_phone.toInt(),
                        vis_type,
                        vis_idNumber

                    )


                    visitorViewModel.insert(visitor)


                    Toast.makeText(applicationContext, "saved successfully", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        " visitor not saved successfully",
                        Toast.LENGTH_LONG
                    ).show()

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

