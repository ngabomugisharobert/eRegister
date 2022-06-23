package com.example.eregister.data.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.eregister.data.entities.guard.Guard
import com.example.eregister.data.repositories.GuardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GuardViewModel(private val guardRepository: GuardRepository) : ViewModel() {


    fun insert(guard: Guard) = CoroutineScope(Dispatchers.IO).launch {
        guardRepository.insert(guard)
    }

    fun checkLogin(username: String, password: String) = CoroutineScope(Dispatchers.IO).launch{
        guardRepository.checkLogin(username, password)
    }

}

class GuardViewModelFactory(private val guardRepository: GuardRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GuardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GuardViewModel(guardRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
