package com.example.eregister.data.repositories

import androidx.annotation.WorkerThread
import com.example.eregister.data.dao.MovementDao
import com.example.eregister.data.entities.movement.Movement
import kotlinx.coroutines.flow.Flow

class MovementRepository(private val movementDao: MovementDao) {

//    val allMovements: Flow<List<Movement>> = movementDao.allMovements()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(movement: Movement) {
        movementDao.insert(movement)
    }


    fun allMovements():Flow<List<Movement>> {
        return movementDao.allMovements()
    }



    companion object{
        private val TAG:String = MovementRepository::class.java.simpleName
    }

}