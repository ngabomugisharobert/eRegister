package com.hogl.eregister.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import com.hogl.eregister.R
import com.hogl.eregister.utils.SessionManagement
import com.hogl.eregister.User
import com.hogl.eregister.data.InitApplication
import com.hogl.eregister.data.entities.movement.Movement
import com.hogl.eregister.data.models.MovementViewModel
import com.hogl.eregister.data.models.MovementViewModelFactory
import com.hogl.eregister.utils.GenerateVisitorId
import com.google.gson.Gson
import com.hogl.eregister.databinding.ActivityHomeBinding
import com.hogl.eregister.databinding.ActivityMovementRecordBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MovementRecordActivity : AppCompatActivity() {

    lateinit var VISITOR_ID: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesCheckIn: SharedPreferences
    private lateinit var checkInListeditor: SharedPreferences.Editor

    private val options = arrayOf("Walk", "Bicycle", "Motorbike", "Car")

    private lateinit var user: User
    private lateinit var transport_type_value: String
    private lateinit var transportType: Spinner
    private lateinit var btn_movement_record_check_in: Button
    private lateinit var btn_movement_record_check_Out: Button
    private lateinit var txt_plate_number: EditText


    private val movementViewModel: MovementViewModel by viewModels {
        MovementViewModelFactory((this.application as InitApplication).movementRepository)
    }

    private lateinit var binding: ActivityMovementRecordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMovementRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()



        val sessionManagement = SessionManagement(this@MovementRecordActivity)
        val guardID: Int = sessionManagement.session


        //get visitor id from previous activity
        VISITOR_ID = intent.getStringExtra("VISITOR_ID").toString()

        var isVisitorCheckedIn :Boolean = sharedPreferencesCheckIn.getInt(VISITOR_ID,0) !=0

        if (isVisitorCheckedIn)
        {
            btn_movement_record_check_Out.isEnabled = true
            btn_movement_record_check_in.isEnabled = false
        }
        else
        {
            btn_movement_record_check_Out.isEnabled = false
            btn_movement_record_check_in.isEnabled = true
        }

        transportType.adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_list_item_1, options
        )
        transportType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                transport_type_value = options[p2]
                if (transport_type_value == "Car" || transport_type_value == "Motorbike") {
                    txt_plate_number.visibility = View.VISIBLE
                } else {
                    txt_plate_number.visibility = View.GONE
                }
            }

            //if nothing is selected, set default value
            override fun onNothingSelected(p0: AdapterView<*>?) {
                transport_type_value = options[0]
                if (transport_type_value == "Car" || transport_type_value == "Motorbike") {
                    txt_plate_number.visibility = View.VISIBLE
                } else {
                    txt_plate_number.visibility = View.GONE
                }
            }
        }

        //save movement to database
        btn_movement_record_check_in.setOnClickListener {
            val plateNumber = txt_plate_number.text.toString()
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val formatted = current.format(formatter)

            var gson: Gson = Gson()
            var data: String? = sharedPreferences.getString("USER", null)
            if (data != null) {
                user = gson.fromJson(data, User::class.java)
                movementViewModel.insert(
                    Movement(
                        GenerateVisitorId.getId(),
                        VISITOR_ID.toInt(),
                        user.gate_id,
                        formatted,
                        guardID,
                        transport_type_value,
                        plateNumber,
                        "check-in",
                        System.currentTimeMillis()
                    )
                )
                with(sharedPreferencesCheckIn.edit()){
                    putInt(VISITOR_ID,VISITOR_ID.toInt())
                    apply()
                }
                Toast.makeText(
                    this@MovementRecordActivity,
                    R.string.checkInDone,
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(this@MovementRecordActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }

        btn_movement_record_check_Out.setOnClickListener {
            val plateNumber = txt_plate_number.text.toString()
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val formatted = current.format(formatter)

            var gson: Gson = Gson()
            var data: String? = sharedPreferences.getString("USER", null)
            if (data != null) {
                user = gson.fromJson(data, User::class.java)
                movementViewModel.insert(
                    Movement(
                        GenerateVisitorId.getId(),
                        VISITOR_ID.toInt(),
                        user.gate_id,
                        formatted,
                        guardID,
                        transport_type_value,
                        plateNumber,
                        "check-out",
                        System.currentTimeMillis()
                    )
                )
                with(sharedPreferencesCheckIn.edit()){
                    remove(VISITOR_ID)
                    apply()
                }
                Toast.makeText(
                    this@MovementRecordActivity,
                    R.string.checkOutDone,
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(this@MovementRecordActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
        }
    }

    private fun initComponents() {

        //get views for plate number from layout
        transportType = binding.transportType
        txt_plate_number = binding.txtPlateNumber
        btn_movement_record_check_in = binding.btnRecordCheckInMovement
        btn_movement_record_check_Out = binding.btnRecordCheckOutMovement

        btn_movement_record_check_in.isEnabled = false
        btn_movement_record_check_Out.isEnabled = false

        sharedPreferences =
            applicationContext.getSharedPreferences("preferences", Context.MODE_PRIVATE)

        sharedPreferencesCheckIn=
            applicationContext.getSharedPreferences("CheckedInList", Context.MODE_PRIVATE)

        checkInListeditor = sharedPreferencesCheckIn.edit()

    }

    companion object {
        private val TAG: String = MovementRecordActivity::class.java.simpleName
    }
}