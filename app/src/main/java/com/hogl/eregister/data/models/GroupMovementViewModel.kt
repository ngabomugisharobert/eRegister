package com.hogl.eregister.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.hogl.eregister.data.entities.Group
import com.hogl.eregister.data.entities.GroupMovement
import com.hogl.eregister.data.repositories.GroupMovementRepository
import com.hogl.eregister.data.repositories.MovementRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GroupMovementViewModel(private val groupMovementRepository: GroupMovementRepository): ViewModel() {


    val allGroupMovements: LiveData<List<GroupMovement>> = groupMovementRepository.allGroupMovements().asLiveData()

    fun insert(groupMovement: GroupMovement) = CoroutineScope(Dispatchers.IO).launch {
        groupMovementRepository.insert(groupMovement)
    }

    fun groupMovementsToSync(timestamp:Long):LiveData<List<GroupMovement>>
    {
        return groupMovementRepository.groupMovementsToSync(timestamp).asLiveData()
    }

    fun groupMovementsList():LiveData<List<GroupMovement>>{
        return groupMovementRepository.allGroupMovements().asLiveData()
    }

    fun getGRoupMovementsByGroupId(grpId: String):LiveData<List<GroupMovement>>{
        return groupMovementRepository.getGRoupMovementsByGroupId(grpId).asLiveData()
    }

    fun getGRoupMovementsByGroupIdType(grpId: String, s: String): LiveData<List<GroupMovement>>{
        return groupMovementRepository.getGRoupMovementsByGroupIdType(grpId, s).asLiveData()
    }

    fun groupMovementToSync(timestamp:Long): LiveData<List<GroupMovement>>{
        var a = groupMovementRepository.groupMovementToSync(timestamp).asLiveData()
        return a
    }

}

class GroupMovementViewModelFactory(private val repository: GroupMovementRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupMovementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupMovementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}