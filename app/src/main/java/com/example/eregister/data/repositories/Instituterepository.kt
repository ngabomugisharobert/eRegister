package com.example.eregister.data.repositories



import androidx.annotation.WorkerThread
import com.example.eregister.data.dao.InstituteDao
import com.example.eregister.data.entities.institute.Institute

class InstituteRepository(private val instituteDao: InstituteDao) {


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(institute: Institute) {
        instituteDao.insert(institute)
    }

}