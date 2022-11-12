package com.hogl.eregister.data.repositories

import androidx.annotation.WorkerThread
import com.hogl.eregister.data.dao.GroupDao
import com.hogl.eregister.data.entities.Group
import com.hogl.eregister.data.entities.visitor.Visitor
import kotlinx.coroutines.flow.Flow

class GroupRepository(private val groupDao: GroupDao) {

//    val allGroups: Flow<List<Group>> = groupDao.allGroups()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(group: Group) {
        groupDao.insert(group)
    }

    fun groupsToSync(timestamp: Long): Flow<List<Group>> {
        return groupDao.groupsToSync(timestamp)
    }

    fun allGroups():Flow<List<Group>> {
        return groupDao.allGroups()
    }

    fun groupToSync(timestamp: Long): Flow<List<Group>> {
        return groupDao.groupsToSync(timestamp)
    }


    companion object{
        private val TAG:String = GroupRepository::class.java.simpleName
    }

}