package com.example.eregister.data.repositories

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.eregister.data.dao.VisitorDao
import com.example.eregister.data.entities.visitor.Visitor
import com.example.eregister.lifecycle.MainActivityObserver
import kotlinx.coroutines.flow.Flow

class VisitorRepository(private val visitorDao: VisitorDao) {

//    val allVisitors: Flow<List<Visitor>> = visitorDao.allVisitors()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(visitor: Visitor) {
        visitorDao.insert(visitor)
    }


    fun findVisitorByName(vis_name:String):Flow<List<Visitor>> {
       return visitorDao.findVisitorByName(vis_name)
    }

    fun allVisitors():Flow<List<Visitor>> {
        return visitorDao.allVisitors()
    }

    fun searchDatabase(vis_name: String): Flow<List<Visitor>> {
        return visitorDao.searchDatabase(vis_name)
    }

    companion object{
        private val TAG:String = VisitorRepository::class.java.simpleName
    }

}