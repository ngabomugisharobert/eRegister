package com.example.eregister.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import com.example.eregister.R
import com.example.eregister.SessionManagement
import com.example.eregister.data.InitApplication
import com.example.eregister.data.entities.movement.Movement
import com.example.eregister.data.models.MovementViewModel
import com.example.eregister.data.models.MovementViewModelFactory
import com.example.eregister.utils.GenerateVisitorId
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MovementRecordActivity : AppCompatActivity() {

    lateinit var VISITOR_ID: String

    private val options = arrayOf("Walk", "Bicycle", "Motorbike", "Car")

    lateinit var transport_type_value: String
    lateinit var transportType: Spinner
    lateinit var btn_movement_record_check_in: Button
    lateinit var btn_movement_record_check_Out: Button
    lateinit var txt_plate_number: EditText


    private val movementViewModel: MovementViewModel by viewModels {
        MovementViewModelFactory((this.application as InitApplication).movementRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movement_record)


        val sessionManagement = SessionManagement(this@MovementRecordActivity)
        val guardID: Int = sessionManagement.session


        //get visitor id from previous activity
        VISITOR_ID = intent.getStringExtra("VISITOR_ID").toString()

        //get views for plate number from layout
        transportType = findViewById(R.id.transport_type)
        txt_plate_number = findViewById(R.id.txt_plate_number)
        btn_movement_record_check_in = findViewById(R.id.btn_record_checkIn_movement)
        btn_movement_record_check_Out = findViewById(R.id.btn_record_checkOut_movement)

        transportType.adapter = ArrayAdapter<String>(
            this, android.R.layout.simple_list_item_1, options
        )
        transportType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                transport_type_value = options[p2]
                if(transport_type_value == "Car" || transport_type_value == "Motorbike"){
                    txt_plate_number.visibility = View.VISIBLE
                }else{
                    txt_plate_number.visibility = View.GONE
                }
            }

            //if nothing is selected, set default value
            override fun onNothingSelected(p0: AdapterView<*>?) {
                transport_type_value = options[0]
                if(transport_type_value == "Car" || transport_type_value == "Motorbike"){
                    txt_plate_number.visibility = View.VISIBLE
                }
                else{
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
            movementViewModel.insert(
                Movement(
                    GenerateVisitorId.getId(),
                    VISITOR_ID.toInt(),
                    32,
                    formatted,
                    guardID,
                    transport_type_value,
                    plateNumber,
                    "check-in",
                )
            )

            Toast.makeText(this@MovementRecordActivity, "Check-In successful", Toast.LENGTH_LONG).show()
            val intent = Intent(this@MovementRecordActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }

        btn_movement_record_check_Out.setOnClickListener {
            val plateNumber = txt_plate_number.text.toString()
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            val formatted = current.format(formatter)
            movementViewModel.insert(
                Movement(
                    GenerateVisitorId.getId(),
                    VISITOR_ID.toInt(),
                    32,
                    formatted,
                    guardID,
                    transport_type_value,
                    plateNumber,
                    "check-out",
                )
            )

            Toast.makeText(this@MovementRecordActivity, "Check-out successful", Toast.LENGTH_LONG).show()
            val intent = Intent(this@MovementRecordActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    companion object {
        private val TAG: String = MovementRecordActivity::class.java.simpleName
    }
}