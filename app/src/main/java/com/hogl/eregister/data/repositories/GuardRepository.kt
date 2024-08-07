package com.hogl.eregister.data.repositories


import android.util.Log
import androidx.annotation.WorkerThread
import com.hogl.eregister.activities.LoginActivity
import com.hogl.eregister.data.dao.GuardDao
import com.hogl.eregister.data.entities.guard.Guard
import kotlinx.coroutines.flow.Flow

class GuardRepository(private val guardDao: GuardDao) {


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(guard: Guard) {
        guardDao.insert(guard)
    }

    fun allGuards():Flow<List<Guard>> {
        return guardDao.allGuards()
    }

    fun checkLogin(username: String, password: String): Flow<Guard> {
        var result = guardDao.checkLogin(username, password)
        Log.i(TAG, result.toString() + "&&&&&&&&&&&&&&&&&&&&&&&&&")


        return result
    }

    fun updateGuardPassword(gua_id: Int, gua_password: String) {
        guardDao.updateGuardPassword(gua_id, gua_password)
    }

    companion object {
        private val TAG: String = LoginActivity::class.java.simpleName
    }

}