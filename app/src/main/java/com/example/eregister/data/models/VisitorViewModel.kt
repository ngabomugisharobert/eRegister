package com.example.eregister.data.models

import android.util.Log
import androidx.lifecycle.*
import com.example.eregister.data.entities.visitor.Visitor
import com.example.eregister.data.repositories.VisitorRepository
import com.example.eregister.lifecycle.MainActivityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class VisitorViewModel(private val repository: VisitorRepository): ViewModel() {


    val allVisitors: LiveData<List<Visitor>> = repository.allVisitors().asLiveData()

    fun insert(visitor: Visitor) = CoroutineScope(Dispatchers.IO).launch {
        repository.insert(visitor)
    }


    fun findVisitorByName(vis_name:String): LiveData<List<Visitor>> {
       return repository.findVisitorByName(vis_name).asLiveData()
    }

    fun visitorsList():LiveData<List<Visitor>>{
        return repository.allVisitors().asLiveData()
    }

    fun searchDatabase(searchQuery: String): LiveData<List<Visitor>> {
        return repository.searchDatabase(searchQuery).asLiveData()
    }

}

class VisitorViewModelFactory(private val repository: VisitorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisitorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VisitorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
