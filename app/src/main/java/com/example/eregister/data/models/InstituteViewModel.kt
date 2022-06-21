package com.example.eregister.data.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eregister.data.entities.guard.Guard
import com.example.eregister.data.entities.institute.Institute
import com.example.eregister.data.repositories.GuardRepository
import com.example.eregister.data.repositories.InstituteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class InstituteViewModel(private val repository: InstituteRepository): ViewModel() {


//    val allVisitors: LiveData<List<Visitor>> = repository.allVisitors.asLiveData()

    fun insert(institute: Institute) = CoroutineScope(Dispatchers.IO).launch {
        repository.insert(institute)
    }

}

class InstituteViewModelFactory(private val repository: InstituteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InstituteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InstituteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
