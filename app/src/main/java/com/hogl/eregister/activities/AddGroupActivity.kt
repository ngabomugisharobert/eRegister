package com.hogl.eregister.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.hogl.eregister.R
import com.hogl.eregister.databinding.ActivityGroupAddBinding

class AddGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupAddBinding

    private lateinit var btnSaveGroup: Button
    private lateinit var tagType: Spinner
    private var choosenType: String = ""
    private lateinit var grp_name: EditText
    private lateinit var grp_leader: EditText
    private lateinit var grp_members: EditText

    var options =
        arrayOf("QR Code")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()

        onClickListerners()

        tagType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                choosenType = options.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                choosenType = options[0]
            }

        }

    }

    private fun onClickListerners() {
        btnSaveGroup.setOnClickListener {
            //save Group in the database

            var resultIntent = Intent()
            if (
                TextUtils.isEmpty(grp_name.text) ||
                TextUtils.isEmpty(grp_leader.text) ||
                TextUtils.isEmpty(grp_members.text)
            ) {
                Toast.makeText(this, R.string.allField, Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_CANCELED, resultIntent)
            } else {
                resultIntent.putExtra(AddGroupActivity.GRP_NAME, grp_name.text.toString())
                resultIntent.putExtra(AddGroupActivity.GRP_LEADER, grp_leader.text.toString())
                resultIntent.putExtra(AddGroupActivity.GRP_MEMBERS, grp_members.text.toString())
                resultIntent.putExtra(AddGroupActivity.GRP_TAG_TYPE, choosenType)
                setResult(Activity.RESULT_OK, resultIntent)
            }
            finish()
        }
    }

    private fun initComponents() {
        this.title = "Group"

        grp_name = findViewById(R.id.grp_name)
        grp_leader = findViewById(R.id.grp_leader)
        grp_members = findViewById(R.id.grp_members)
        grp_members.setInputType(InputType.TYPE_CLASS_NUMBER)
        btnSaveGroup = binding.btnSaveGroup
        tagType = binding.tagType

        tagType.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)
    }

    companion object {
        private val TAG: String = AddGroupActivity::class.java.simpleName
        const val GRP_NAME = "grp_name"
        const val GRP_LEADER = "grp_leader"
        const val GRP_MEMBERS = "grp_members"
        const val GRP_TAG_TYPE = "grp_tag_type"
    }
}