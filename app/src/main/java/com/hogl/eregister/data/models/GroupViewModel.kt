package com.hogl.eregister.data.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.hogl.eregister.data.entities.Group
import com.hogl.eregister.data.repositories.GroupRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GroupViewModel(private val groupRepository: GroupRepository): ViewModel() {


    val allGroups: LiveData<List<Group>> = groupRepository.allGroups().asLiveData()

    fun insert(group: Group) = CoroutineScope(Dispatchers.IO).launch {
        groupRepository.insert(group)
    }

    fun groupsToSync(timestamp:Long):LiveData<List<Group>>
    {
        return groupRepository.groupsToSync(timestamp).asLiveData()
    }

    fun groupsList():LiveData<List<Group>>{
        return groupRepository.allGroups().asLiveData()
    }


}

class GroupViewModelFactory(private val groupRepository: GroupRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GroupViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GroupViewModel(groupRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
