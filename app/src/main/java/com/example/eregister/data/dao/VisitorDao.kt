package com.example.eregister.data.dao

import androidx.room.*
import com.example.eregister.data.entities.visitor.Visitor


@Dao
abstract class VisitorDao : BaseDao<Visitor>() {
    @get:Query("SELECT * from tb_visitors")
    abstract val visitors: List<Visitor>
}