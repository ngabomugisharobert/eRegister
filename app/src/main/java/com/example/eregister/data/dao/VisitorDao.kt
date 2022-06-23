package com.example.eregister.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.eregister.data.entities.visitor.Visitor
import kotlinx.coroutines.flow.Flow


@Dao
abstract class VisitorDao : BaseDao<Visitor>() {

    @Query("SELECT * from tb_visitors")
    abstract fun allVisitors(): Flow<List<Visitor>>

    @Query("SELECT * FROM tb_visitors WHERE vis_first_name LIKE :vis_name OR vis_last_name LIKE :vis_name")
    abstract fun findVisitorByName(vis_name: String): Flow<List<Visitor>>

//    @Query("SELECT * FROM tb_visitors WHERE vis_first_name LIKE :vis_name OR vis_last_name LIKE :vis_name")
//    abstract fun searchDatabase(vis_name: String): Flow<List<Visitor>>

}