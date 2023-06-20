package com.hogl.eregister.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.hogl.eregister.data.entities.guard.Guard
import com.hogl.eregister.data.repositories.GuardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GuardViewModel(private val guardRepository: GuardRepository) : ViewModel() {


    val allGuards: LiveData<List<Guard>> = guardRepository.allGuards().asLiveData()

    lateinit var res: Guard
    fun insert(guard: Guard) = CoroutineScope(Dispatchers.IO).launch {
        guardRepository.insert(guard)
    }

    fun checkLogin(username: String, password: String): LiveData<Guard> {
        return guardRepository.checkLogin(username, password).asLiveData()
    }

    fun updateGuardPassword(gua_id: Int, gua_password: String) = CoroutineScope(Dispatchers.IO).launch {
        guardRepository.updateGuardPassword(gua_id, gua_password)
    }

}

class GuardViewModelFactory(private val guardRepository: GuardRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GuardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GuardViewModel(guardRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

    companion object {
        private val TAG: String = GuardViewModel::class.java.simpleName
    }

}
