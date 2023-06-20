package com.hogl.eregister.data.models

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.hogl.eregister.VisitorClass
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

    fun visitorToSync(): LiveData<List<Visitor>>{
        return visitorRepository.visitorToSync().asLiveData()

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

    fun getVisitorByTag(tagId: String): LiveData<Visitor> {
        return visitorRepository.findVisitorByTag(tagId).asLiveData()
    }

    fun getVisitorById(visId: String): LiveData<Visitor> {
        var id:Int = 0
        //convert string to int
        try {
             id = visId.toInt()
        }catch (e: Exception){
            Log.e("VisitorViewModelERROR", "getVisitorById: " + e.message)
        }
        finally {
            return visitorRepository.findVisitorById(id).asLiveData()
        }
    }

    fun getVisitorByNfc(tagId: String): LiveData<Visitor> {
        return visitorRepository.findVisitorByNfc(tagId).asLiveData()
    }
    fun updateVisitor(visitor: Visitor) = CoroutineScope(Dispatchers.IO).launch {
        visitorRepository.updateVisitor(visitor)
    }

    fun insertAll(visitors2: MutableList<Visitor>) {
        visitorRepository.insertAll(visitors2)
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
