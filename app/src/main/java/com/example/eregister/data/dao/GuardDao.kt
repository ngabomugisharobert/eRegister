package com.example.eregister.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.eregister.data.entities.guard.Guard


@Dao
abstract class GuardDao : BaseDao<Guard>() {
    @get:Query("SELECT * from tb_guards")
    abstract val guards: List<Guard>
}