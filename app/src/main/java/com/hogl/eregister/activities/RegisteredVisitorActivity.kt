package com.hogl.eregister.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.hogl.eregister.R
import com.hogl.eregister.adapter.VisitorAdapter
import com.hogl.eregister.data.InitApplication
import com.hogl.eregister.data.entities.visitor.Visitor
import com.hogl.eregister.data.models.VisitorViewModel
import com.hogl.eregister.data.models.VisitorViewModelFactory
import com.hogl.eregister.databinding.ActivityNewVisitorBinding
import com.hogl.eregister.databinding.ActivityRegisteredVisitorBinding


class RegisteredVisitorActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var recyclerView: RecyclerView
    private val visitorsListViewModel by viewModels<VisitorViewModel> {
        VisitorViewModelFactory((application as InitApplication).visitorRepository)
    }

    private val visitorAdapter = VisitorAdapter { visitor -> adapterOnClick(visitor) }

    private lateinit var binding: ActivityRegisteredVisitorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityRegisteredVisitorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()

        visitorsListViewModel.allVisitors.observe(this) {
            it.let {
                visitorAdapter.submitList(it as MutableList<Visitor>)
            }
        }
    }

    private fun adapterOnClick(visitor: Visitor) {
        val intent = Intent(this, MovementRecordActivity::class.java)
        intent.putExtra("VISITOR_ID", visitor.vis_id.toString())
        intent.putExtra("VISITOR_FNAME", visitor.vis_first_name)
        intent.putExtra("VISITOR_LNAME", visitor.vis_last_name)
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.visitor_menu, menu)

        val search = menu.findItem(R.id.menu_search)
        val searchView = search?.actionView as? SearchView
        searchView?.isSubmitButtonEnabled = true
        searchView?.setOnQueryTextListener(this)
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null){
            searchDatabase(query)
        }
        return true
    }

    private fun searchDatabase(query: String) {
        val vis_name = "%$query%"
        visitorsListViewModel.searchDatabase(vis_name).observe(this) { list ->
            list.let {
                visitorAdapter.submitList(it as MutableList<Visitor>)
            }
        }
    }

    private fun initComponents(){
        recyclerView = binding.recyclerView
        recyclerView.adapter = visitorAdapter
    }

    companion object{
        private val TAG:String = RegisteredVisitorActivity::class.java.simpleName
    }
}














