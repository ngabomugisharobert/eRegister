package com.example.eregister.data.repositories

import androidx.annotation.WorkerThread
import com.example.eregister.data.dao.VisitorDao
import com.example.eregister.data.entities.visitor.Visitor

class VisitorRepository(private val visitorDao: VisitorDao) {

//    val allVisitors: Flow<List<Visitor>> = visitorDao.getAlphabetizedVisitors()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(visitor: Visitor) {
        visitorDao.insert(visitor)
    }

}