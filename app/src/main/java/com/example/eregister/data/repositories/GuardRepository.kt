package com.example.eregister.data.repositories


import androidx.annotation.WorkerThread
import com.example.eregister.data.dao.GuardDao
import com.example.eregister.data.entities.guard.Guard

class GuardRepository(private val guardDao: GuardDao) {


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(guard: Guard) {
        guardDao.insert(guard)
    }

    fun checkLogin(username: String, password:String): Boolean {
        return guardDao.checkLogin(username,password)
    }

}