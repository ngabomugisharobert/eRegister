package com.hogl.eregister.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import com.hogl.eregister.R
import com.hogl.eregister.data.InitApplication
import com.hogl.eregister.data.models.GroupMovementViewModel
import com.hogl.eregister.data.models.GroupMovementViewModelFactory
import com.hogl.eregister.databinding.ActivityGroupDetailsBinding
import com.hogl.eregister.databinding.ActivityGroupsBinding

class GroupDetailsActivity : AppCompatActivity() {



    private lateinit var grp_id: String
    private lateinit var grp_name: String
    private lateinit var grp_leader: String
    private lateinit var grp_members: String

    private lateinit var members_number:TextView
    private lateinit var btn_check_in_group : Button
    private lateinit var btn_check_out_group : Button
    private lateinit var txt_check_in : TextView
    private lateinit var txt_check_out : TextView

    private var check_in_number = 0
    private var check_out_number = 0

    private val groupMovementViewModel: GroupMovementViewModel by viewModels {
        GroupMovementViewModelFactory((this.application as InitApplication).groupMovementRepository)
    }

    private lateinit var binding: ActivityGroupDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        onClickListerners()



    }

    private fun onClickListerners() {
        btn_check_in_group.setOnClickListener {
            var intent : Intent = Intent(this, ScanActivity::class.java)
            intent.putExtra("ACTION", "GROUP_CHECK_IN")
            intent.putExtra("GROUP_ID", grp_id)
            startActivity(intent)
        }

        btn_check_out_group.setOnClickListener {
            var intent : Intent = Intent(this, ScanActivity::class.java)
            intent.putExtra("ACTION", "GROUP_CHECK_OUT")
            intent.putExtra("GROUP_ID", grp_id)
            startActivity(intent)
        }
    }

    private fun initComponents() {

        grp_id = intent.getStringExtra("GRP_ID").toString()
        grp_name = intent.getStringExtra("GRP_NAME").toString()
        grp_leader = intent.getStringExtra("GRP_LEADER").toString()
        grp_members = intent.getStringExtra("GRP_MEMBERS").toString()

        members_number = binding.membersNumber
        btn_check_in_group = binding.btnCheckInGroup
        btn_check_out_group = binding.btnCheckOutGroup
        txt_check_in = binding.txtCheckIn
        txt_check_out = binding.txtCheckOut


        txt_check_in.text = check_in_number.toString()
        txt_check_out.text = check_out_number.toString()
        members_number.text = grp_members

        this.title = "Group : $grp_name"

        groupMovementViewModel.getGRoupMovementsByGroupIdType(grp_id,"CHECK_IN").observe(this) { group ->
            if (group != null) {
                check_in_number = group.size
                txt_check_in.text = check_in_number.toString()
            }
            btn_check_out_group.isEnabled = !(check_in_number == check_out_number)
        }
        groupMovementViewModel.getGRoupMovementsByGroupIdType(grp_id,"CHECK_OUT").observe(this) { group ->
            if (group != null) {
                check_out_number = group.size
                txt_check_out.text = check_out_number.toString()
            }
            btn_check_in_group.isEnabled = check_in_number != grp_members.toInt()
            btn_check_out_group.isEnabled = !(check_in_number == check_out_number)
        }
    }
}