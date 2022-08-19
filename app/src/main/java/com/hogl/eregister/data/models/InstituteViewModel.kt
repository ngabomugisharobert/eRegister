package com.hogl.eregister.data.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hogl.eregister.data.entities.Institute
import com.hogl.eregister.data.repositories.InstituteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class InstituteViewModel(private val repository: InstituteRepository): ViewModel() {


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
