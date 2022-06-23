package com.example.eregister.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.example.eregister.R
import com.example.eregister.adapter.VisitorAdapter
import com.example.eregister.data.InitApplication
import com.example.eregister.data.entities.visitor.Visitor
import com.example.eregister.data.models.VisitorViewModel
import com.example.eregister.data.models.VisitorViewModelFactory


class RegisteredVisitorActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    private val newVisitorActivityRequestCode = 1
    private val visitorsListViewModel by viewModels<VisitorViewModel> {
        VisitorViewModelFactory((application as InitApplication).visitorRepository)
    }


    private val visitorAdapter = VisitorAdapter { visitor -> adapterOnClick(visitor) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registered_visitor)


        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.adapter = visitorAdapter

        visitorsListViewModel.allVisitors.observe(this) {
            it.let {
                visitorAdapter.submitList(it as MutableList<Visitor>)
            }
        }
    }

    private fun adapterOnClick(visitor: Visitor) {
        val intent = Intent(this, MovementRecordActivity::class.java)
        intent.putExtra("VISITOR_ID", visitor.vis_id.toString())
        Log.i(TAG, " ------------ >Visitor id: ${visitor.vis_id}")
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.visitor_menu, menu)

        val search = menu?.findItem(R.id.menu_search)
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

    companion object{
        private val TAG:String = RegisteredVisitorActivity::class.java.simpleName
    }


}














