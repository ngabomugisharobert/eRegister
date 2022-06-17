package com.example.eregister.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.eregister.data.entities.institute.Institute


@Dao
abstract class InstituteDao : BaseDao<Institute>() {
    @get:Query("SELECT * from tb_institutes")
    abstract val institutes: List<Institute>
}