package com.example.eregister.data.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eregister.data.entities.guard.Guard
import com.example.eregister.data.entities.visitor.Visitor
import com.example.eregister.data.repositories.GuardRepository
import com.example.eregister.data.repositories.VisitorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GuardViewModel(private val repository: GuardRepository): ViewModel() {


//    val allVisitors: LiveData<List<Visitor>> = repository.allVisitors.asLiveData()

    fun insert(guard: Guard) = CoroutineScope(Dispatchers.IO).launch {
        repository.insert(guard)
    }

}

class GuardViewModelFactory(private val repository: GuardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GuardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GuardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
