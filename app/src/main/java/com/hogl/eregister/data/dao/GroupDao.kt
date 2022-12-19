package com.hogl.eregister.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.hogl.eregister.data.entities.Group
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GroupDao :BaseDao<Group>() {

    @Query("SELECT * from tb_groups")
    abstract fun allGroups(): Flow<List<Group>>
//
//    @Query("SELECT * from tb_groups where timestamp > :timestamp")
//    abstract fun groupsToSync(timestamp: Long): Flow<List<Group>>


    @Query("SELECT * from tb_groups")
    abstract fun groupsToSync(): Flow<List<Group>>
}