package com.hogl.eregister.data.repositories

import androidx.annotation.WorkerThread
import com.hogl.eregister.data.dao.VisitorDao
import com.hogl.eregister.data.entities.visitor.Visitor
import kotlinx.coroutines.flow.Flow

class VisitorRepository(private val visitorDao: VisitorDao) {

//    val allVisitors: Flow<List<Visitor>> = visitorDao.allVisitors()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(visitor: Visitor) {
        visitorDao.insert(visitor)
    }

    fun visitorToSync(timestamp:String):Flow<List<Visitor>>
    {
        return visitorDao.visitorToSync(timestamp)
    }

    fun findVisitorByName(vis_name:String):Flow<List<Visitor>> {
       return visitorDao.findVisitorByName(vis_name)
    }

    fun allVisitors():Flow<List<Visitor>> {
        return visitorDao.allVisitors()
    }


    companion object{
        private val TAG:String = VisitorRepository::class.java.simpleName
    }

}