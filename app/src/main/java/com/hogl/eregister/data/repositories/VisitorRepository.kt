package com.hogl.eregister.data.repositories

import androidx.annotation.WorkerThread
import com.hogl.eregister.VisitorClass
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

    fun visitorToSync(): Flow<List<Visitor>> {
        return visitorDao.visitorToSync()
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
        val a = visitorDao.findVisitorById(visId.toInt())
        return a
    }

    fun findVisitorByNfc(tagId: String): Flow<Visitor> {
        val a = visitorDao.findVisitorByNfc(tagId)
        return a
    }

    fun updateVisitor(visitor: Visitor) {
        visitorDao.updateVisitor(visitor.vis_id.toInt(), visitor.vis_first_name, visitor.vis_last_name, visitor.vis_phone, visitor.vis_type, visitor.vis_IDNumber, visitor.vis_nfc_card)
    }

    fun insertAll(visitors2: MutableList<Visitor>) {
//        loop through the list of visitors and insert each one
        for (visitor in visitors2) {
            visitorDao.insert(visitor)
        }

    }

    companion object {
        private val TAG: String = VisitorRepository::class.java.simpleName
    }

}