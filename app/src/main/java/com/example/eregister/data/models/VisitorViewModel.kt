package com.example.eregister.data.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eregister.data.entities.visitor.Visitor
import com.example.eregister.data.repositories.VisitorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VisitorViewModelModel(private val repository: VisitorRepository): ViewModel() {


//    val allVisitors: LiveData<List<Visitor>> = repository.allVisitors.asLiveData()

    fun insert(visitor: Visitor) = CoroutineScope(Dispatchers.IO).launch {
        repository.insert(visitor)
    }

}

class VisitorViewModelFactory(private val repository: VisitorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisitorViewModelModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VisitorViewModelModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
