package com.hogl.eregister.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.hogl.eregister.R
import com.hogl.eregister.adapter.VisitorAdapter
import com.hogl.eregister.data.InitApplication
import com.hogl.eregister.data.entities.Visitor
import com.hogl.eregister.data.models.VisitorViewModel
import com.hogl.eregister.data.models.VisitorViewModelFactory
import com.hogl.eregister.databinding.ActivityRegisteredVisitorBinding


class RegisteredVisitorActivity : AppCompatActivity(), SearchView.OnQueryTextListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var search_empty : LottieAnimationView
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
                if (it.isEmpty()) {
                search_empty.visibility = android.view.View.VISIBLE
                recyclerView.visibility = android.view.View.INVISIBLE
            } else {
                search_empty.visibility = android.view.View.GONE
                recyclerView.visibility = android.view.View.VISIBLE
                    //sort array by name
//                    it.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER, { visitor -> visitor.vis_first_name }))
                visitorAdapter.submitList(it as MutableList<Visitor>)
            }
            }
        }
    }

    private fun adapterOnClick(visitor: Visitor) {
        val intent = Intent(this, MovementRecordActivity::class.java)
        intent.putExtra("VISITOR_ID", visitor.vis_id.toLong())
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
                if (list.isEmpty()) {
                    search_empty.visibility = android.view.View.VISIBLE
                    recyclerView.visibility = android.view.View.INVISIBLE
                } else {
                    search_empty.visibility = android.view.View.GONE
                    recyclerView.visibility = android.view.View.VISIBLE
                    //sort array by name
//                    list.sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER ,{ visitor -> visitor.vis_first_name }))
                    visitorAdapter.submitList(list as MutableList<Visitor>)
                }
            }
        }
    }

    private fun initComponents(){
        recyclerView = binding.recyclerView
        recyclerView.adapter = visitorAdapter
        search_empty = binding.searchEmpty
    }

    companion object{
        private val TAG:String = RegisteredVisitorActivity::class.java.simpleName
    }
}














