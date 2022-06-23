package com.example.eregister.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.eregister.data.entities.guard.Guard
import com.example.eregister.data.entities.movement.Movement
import kotlinx.coroutines.flow.Flow

@Dao
abstract class MovementDao :BaseDao<Movement>() {

    @Query("SELECT * from tb_movements")
    abstract fun allMovements(): Flow<List<Movement>>

}