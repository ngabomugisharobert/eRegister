package com.example.eregister.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import com.example.eregister.R

class NewVisitorActivity : AppCompatActivity() {


    lateinit var visitorType: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_visitor)

        var visitor_type: String = ""

        visitorType = findViewById(R.id.spVisitorType) as Spinner
        var txt_visitor_fn: TextView = findViewById(R.id.txt_visitor_fn)
        var txt_visitor_ln: TextView = findViewById(R.id.txt_visitor_ln)
        var txt_visitor_phone: TextView = findViewById(R.id.txt_visitor_phone)
        var btn_save_visitor: Button = findViewById(R.id.btn_save_visitor)

        val options = arrayOf("option 1", "option 2", "option 3")
        visitorType.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)

        visitorType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                visitor_type = options.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
//                result.text ="Please select visitor type."
            }

        }

        btn_save_visitor.setOnClickListener {
            var resultIntent = Intent()
            if (
                TextUtils.isEmpty(txt_visitor_fn.text) ||
                TextUtils.isEmpty(txt_visitor_ln.text) ||
                TextUtils.isEmpty(txt_visitor_phone.text) ||
                TextUtils.isEmpty(visitor_type)
            ) {
                Log.i(TAG,"*** all fields must be filled ***")
                setResult(Activity.RESULT_CANCELED,resultIntent)
            }else
            {
                resultIntent.putExtra(VIS_FIRST_NAME, txt_visitor_fn.text.toString())
                resultIntent.putExtra(VIS_LAST_NAME, txt_visitor_ln.text.toString())
                resultIntent.putExtra(VIS_PHONE, txt_visitor_phone.text.toString())
                resultIntent.putExtra(VIS_TYPE, visitor_type)
                setResult(Activity.RESULT_OK,resultIntent)
            }
            finish()
        }
    }

    companion object{
        private val TAG:String = NewVisitorActivity::class.java.simpleName
        const val VIS_FIRST_NAME = "vis_first_name"
        const val VIS_LAST_NAME = "vis_last_name"
        const val VIS_PHONE = "vis_phone"
        const val VIS_TYPE = "vis_type"
    }
}