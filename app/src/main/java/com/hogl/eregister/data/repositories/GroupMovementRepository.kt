package com.hogl.eregister.data.repositories

import androidx.annotation.WorkerThread
import com.hogl.eregister.data.dao.GroupMovementDao
import com.hogl.eregister.data.entities.Group
import com.hogl.eregister.data.entities.GroupMovement
import kotlinx.coroutines.flow.Flow

class GroupMovementRepository(private val groupMovementDao: GroupMovementDao) {

//    val allGroupMovements: Flow<List<GroupMovement>> = groupMovementDao.allGroupMovements()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(groupMovement: GroupMovement) {
        groupMovementDao.insert(groupMovement)
    }

    fun groupMovementsToSync(): Flow<List<GroupMovement>> {
        return groupMovementDao.groupMovementsToSync()
    }

    fun allGroupMovements():Flow<List<GroupMovement>> {
        return groupMovementDao.allGroupMovements()
    }

    fun getGRoupMovementsByGroupId(grpId: String): Flow<List<GroupMovement>> {
        return groupMovementDao.getGRoupMovementsByGroupId(grpId)
    }

    fun getGRoupMovementsByGroupIdType(grpId: String, s: String): Flow<List<GroupMovement>> {
        return groupMovementDao.getGRoupMovementsByGroupIdType(grpId.toInt(), s)
    }

    fun groupMovementToSync(): Flow<List<GroupMovement>> {
        return groupMovementDao.groupMovementsToSync()
    }

    companion object{
        private val TAG:String = GroupMovementRepository::class.java.simpleName
    }

}