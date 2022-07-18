package com.hogl.eregister.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.hogl.eregister.data.entities.guard.Guard
import com.hogl.eregister.data.entities.movement.Movement
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MovementDao :BaseDao<Movement>() {

    @Query("SELECT * from tb_movements")
    abstract fun allMovements(): Flow<List<Movement>>

    @Query("SELECT * from tb_movements where timestamp < :timestamp")
    abstract  fun movementsToSync(timestamp: String): Flow<List<Movement>>

}