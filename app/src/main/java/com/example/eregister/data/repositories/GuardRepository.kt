package com.example.eregister.data.repositories


import android.util.Log
import androidx.annotation.WorkerThread
import com.example.eregister.LoginActivity
import com.example.eregister.data.dao.GuardDao
import com.example.eregister.data.entities.guard.Guard
import kotlinx.coroutines.flow.Flow

class GuardRepository(private val guardDao: GuardDao) {


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(guard: Guard) {
        guardDao.insert(guard)
    }

    fun checkLogin(username: String, password: String): Flow<Guard> {
        var result = guardDao.checkLogin(username, password)
        Log.i(TAG, result.toString() + "&&&&&&&&&&&&&&&&&&&&&&&&&")


        return result
    }

    companion object {
        private val TAG: String = LoginActivity::class.java.simpleName
    }

}