package com.hogl.eregister.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.hogl.eregister.data.entities.movement.Movement
import com.hogl.eregister.data.repositories.MovementRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MovementViewModel(private val MovementRepository: MovementRepository): ViewModel() {


    val allMovements: LiveData<List<Movement>> = MovementRepository.allMovements().asLiveData()

    fun insert(movement: Movement) = CoroutineScope(Dispatchers.IO).launch {
        MovementRepository.insert(movement)
    }

    fun movementsToSync(timestamp:String):LiveData<List<Movement>>
    {
        return MovementRepository.movementsToSync(timestamp).asLiveData()
    }

    fun movementsList():LiveData<List<Movement>>{
        return MovementRepository.allMovements().asLiveData()
    }


}

class MovementViewModelFactory(private val repository: MovementRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MovementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
