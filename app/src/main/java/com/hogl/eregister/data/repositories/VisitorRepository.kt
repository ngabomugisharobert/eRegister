package com.hogl.eregister.data.repositories

import androidx.annotation.WorkerThread
import com.hogl.eregister.data.dao.VisitorDao
import com.hogl.eregister.data.entities.Visitor
import kotlinx.coroutines.flow.Flow

class VisitorRepository(private val visitorDao: VisitorDao) {

//    val allVisitors: Flow<List<Visitor>> = visitorDao.allVisitors()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(visitor: Visitor) {
        visitorDao.insert(visitor)
    }

    fun visitorToSync(timestamp: Long): Flow<List<Visitor>> {
        return visitorDao.visitorToSync(timestamp)
    }

    fun findVisitorByName(vis_name: String): Flow<List<Visitor>> {
        return visitorDao.findVisitorByName(vis_name)
    }

    fun allVisitors(): Flow<List<Visitor>> {
        return visitorDao.allVisitors()
    }

    fun findVisitorByTag(tagId: String): Flow<Visitor> {
        val a = visitorDao.findVisitorByTag(tagId)
        return a
    }

    fun findVisitorById(visId :Int): Flow<Visitor> {
        val a = visitorDao.findVisitorById(visId)
        return a
    }

    fun findVisitorByNfc(tagId: String): Flow<Visitor> {
        val a = visitorDao.findVisitorByNfc(tagId)
        return a
    }

    companion object {
        private val TAG: String = VisitorRepository::class.java.simpleName
    }

}