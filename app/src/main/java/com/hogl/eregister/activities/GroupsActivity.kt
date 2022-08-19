package com.hogl.eregister.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.hogl.eregister.R
import com.hogl.eregister.adapter.GroupsAdapter
import com.hogl.eregister.data.InitApplication
import com.hogl.eregister.data.entities.Group
import com.hogl.eregister.data.models.GroupViewModel
import com.hogl.eregister.data.models.GroupViewModelFactory
import com.hogl.eregister.databinding.ActivityGroupsBinding
import kotlinx.android.synthetic.main.activity_groups.*

class GroupsActivity : AppCompatActivity(), SearchView.OnQueryTextListener  {

    private lateinit var resultLauncher: ActivityResultLauncher<Intent?>
    private lateinit var recyclerView: RecyclerView
    private lateinit var search_empty : LottieAnimationView
    private lateinit var btn_add_group : FloatingActionButton

    private val groupsListViewModel by viewModels<GroupViewModel> {
        GroupViewModelFactory((application as InitApplication).groupRepository)
    }

    private val groupAdapter = GroupsAdapter { group -> adapterOnClick(group) }

    private lateinit var binding: ActivityGroupsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        onClickListerners()


        groupsListViewModel.allGroups.observe(this) {
            it.let { it ->
                if (it.isEmpty()) {
                    search_empty.visibility = android.view.View.VISIBLE
                    recyclerView.visibility = android.view.View.INVISIBLE
                } else {
                    search_empty.visibility = android.view.View.GONE
                    recyclerView.visibility = android.view.View.VISIBLE
                    //sort array by name
//                    it.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { visitor -> visitor.vis_first_name }))
                    groupAdapter.submitList(it as MutableList<Group>)
                }
            }
        }
    }

    private fun onClickListerners() {
        btnAddGroup.setOnClickListener {



            var intent : Intent = Intent(this, AddGroupActivity::class.java)
            resultLauncher.launch(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.visitor_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
        return true
    }

    private fun adapterOnClick(group: Group) {
        val intent = Intent(this, GroupDetailsActivity::class.java)

        intent.putExtra("GRP_ID", group.grp_id.toString())
        intent.putExtra("GRP_NAME", group.grp_name.toString())
        intent.putExtra("GRP_LEADER", group.grp_leader.toString())
        intent.putExtra("GRP_MEMBERS", group.grp_members.toString())
        startActivity(intent)
    }

    private fun initComponents() {

        this.title = "Group"

        recyclerView = binding.recyclerView
        recyclerView.adapter = groupAdapter
        search_empty = binding.searchEmpty
        btn_add_group = binding.btnAddGroup

         resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    val grp_name =
                        data?.getStringExtra(AddGroupActivity.GRP_NAME).toString()
                    val grp_leader =
                        data?.getStringExtra(AddGroupActivity.GRP_LEADER).toString()
                    val grp_members =
                        data?.getStringExtra(AddGroupActivity.GRP_MEMBERS).toString()
                    val group = Group(
                        0,
                        grp_name,
                        grp_leader,
                        grp_members.toInt(),
                        System.currentTimeMillis()
                    )
//                    TODO NFC ID CArd and QR CODE TO BE Implemented
                    groupsListViewModel.insert(group)
                    Log.e("GROUP", Gson().toJson(group))
                    Toast.makeText(applicationContext, R.string.saved, Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        R.string.error_saving,
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return true
    }

}