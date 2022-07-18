package com.hogl.eregister.data.models

import android.util.Log
import androidx.lifecycle.*
import com.hogl.eregister.data.entities.visitor.Visitor
import com.hogl.eregister.data.repositories.GuardRepository
import com.hogl.eregister.data.repositories.VisitorRepository
import com.hogl.eregister.lifecycle.MainActivityObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class VisitorViewModel(private val visitorRepository: VisitorRepository): ViewModel() {


    val allVisitors: LiveData<List<Visitor>> = visitorRepository.allVisitors().asLiveData()

    fun insert(visitor: Visitor) = CoroutineScope(Dispatchers.IO).launch {
        visitorRepository.insert(visitor)
    }

    fun visitorToSync(timestamp:String): LiveData<List<Visitor>>{
        return visitorRepository.visitorToSync(timestamp).asLiveData()
    }

    fun findVisitorByName(vis_name:String): LiveData<List<Visitor>> {
       return visitorRepository.findVisitorByName(vis_name).asLiveData()
    }

    fun visitorsList():LiveData<List<Visitor>>{
        return visitorRepository.allVisitors().asLiveData()
    }

    fun searchDatabase(vis_name: String): LiveData<List<Visitor>> {
        return visitorRepository.findVisitorByName(vis_name).asLiveData()
    }

}

class VisitorViewModelFactory(private val visitorRepository: VisitorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisitorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VisitorViewModel(visitorRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
