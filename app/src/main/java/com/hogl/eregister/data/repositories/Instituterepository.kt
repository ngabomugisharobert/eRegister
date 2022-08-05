package com.hogl.eregister.data.repositories



import androidx.annotation.WorkerThread
import com.hogl.eregister.data.dao.InstituteDao
import com.hogl.eregister.data.entities.Institute

class InstituteRepository(private val instituteDao: InstituteDao) {


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(institute: Institute) {
        instituteDao.insert(institute)
    }

}