package com.hogl.eregister.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.hogl.eregister.R
import com.hogl.eregister.databinding.ActivityMovementRecordBinding
import com.hogl.eregister.databinding.ActivityNewVisitorBinding

class NewVisitorActivity : AppCompatActivity() {
    private lateinit var visitorType: Spinner
    private lateinit var txt_visitor_fn: TextInputEditText
    private lateinit var txt_visitor_ln: TextView
    private lateinit var txt_visitor_phone: TextView
    private lateinit var btn_save_visitor: Button
    private lateinit var txt_vis_idNumber: TextView

    private lateinit var options: Array<String>
    private lateinit var visitor_type: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityNewVisitorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNewVisitorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()


        visitorType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                visitor_type = options.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                visitor_type = options[6]
            }

        }

        btn_save_visitor.setOnClickListener {
            var resultIntent = Intent()
            if (
                TextUtils.isEmpty(txt_visitor_fn.text) ||
                TextUtils.isEmpty(txt_visitor_ln.text) ||
                TextUtils.isEmpty(txt_visitor_phone.text) ||
                TextUtils.isEmpty(visitor_type) ||
                TextUtils.isEmpty(txt_vis_idNumber.text)
            ) {
                Toast.makeText(this, R.string.allField, Toast.LENGTH_SHORT).show()
                Log.i(TAG, "*** all fields must be filled ***")
                setResult(Activity.RESULT_CANCELED, resultIntent)
            } else {
                resultIntent.putExtra(VIS_FIRST_NAME, txt_visitor_fn.text.toString())
                resultIntent.putExtra(VIS_LAST_NAME, txt_visitor_ln.text.toString())
                resultIntent.putExtra(VIS_PHONE, txt_visitor_phone.text.toString())
                resultIntent.putExtra(VIS_TYPE, visitor_type)
                resultIntent.putExtra(VIS_ID_NUMBER, txt_vis_idNumber.text.toString())
                setResult(Activity.RESULT_OK, resultIntent)
            }
            finish()
        }
    }

    private fun initComponents() {
        visitorType = binding.spVisitorType
        txt_visitor_fn = binding.txtVisitorFn
        txt_visitor_ln = binding.txtVisitorLn
        txt_visitor_phone = binding.txtVisitorPhone
        btn_save_visitor = binding.btnSaveVisitor
        txt_vis_idNumber = binding.txtVisIdnumber

        visitor_type = ""
        options =
            arrayOf("HOGL employee", "RHL", "HOGL casual", "Authorities", "REG", "Guard", "Other")
        sharedPreferences =
            applicationContext.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        visitorType.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)

    }

    companion object {
        private val TAG: String = NewVisitorActivity::class.java.simpleName
        const val VIS_FIRST_NAME = "vis_first_name"
        const val VIS_LAST_NAME = "vis_last_name"
        const val VIS_PHONE = "vis_phone"
        const val VIS_TYPE = "vis_type"
        const val VIS_ID_NUMBER = "VIS_ID_NUMBER"
    }
}