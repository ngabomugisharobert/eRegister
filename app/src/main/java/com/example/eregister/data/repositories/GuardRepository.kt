package com.example.eregister.data.repositories


import androidx.annotation.WorkerThread
import com.example.eregister.data.dao.GuardDao
import com.example.eregister.data.entities.guard.Guard

class GuardRepository(private val guardDao: GuardDao) {

//    val allVisitors: Flow<List<Visitor>> = visitorDao.getAlphabetizedVisitors()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(guard: Guard) {
        guardDao.insert(guard)
    }

}