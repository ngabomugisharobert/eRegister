package com.example.eregister.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.eregister.R
import com.example.eregister.data.AppDatabase
import com.example.eregister.data.dao.VisitorDao
import com.example.eregister.data.visitor.Visitor

class NewVisitorActivity : AppCompatActivity() {


    lateinit var visitorType:Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_visitor)

        var visitor_type:String = ""

        visitorType = findViewById(R.id.spVisitorType) as Spinner
        var txt_visitor_fn:TextView = findViewById(R.id.txt_visitor_fn)
        var txt_visitor_ln:TextView = findViewById(R.id.txt_visitor_ln)
        var txt_visitor_phone:TextView = findViewById(R.id.txt_visitor_phone)
        var btn_save_visitor:Button = findViewById(R.id.btn_save_visitor)

        val options = arrayOf("option 1", "option 2", "option 3")
        visitorType.adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,options)

        visitorType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                visitor_type = options.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
//                result.text ="Please select visitor type."
            }

        }

        btn_save_visitor.setOnClickListener {
            var vis :Visitor= Visitor(12,"king","rwanda", 0,"32329876",787)
//            var visitorDao = AppDatabase.visitorDao()
//            visitorDao.insertAll(vis)
        }
    }
}