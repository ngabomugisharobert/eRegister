package com.hogl.eregister.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.hogl.eregister.data.entities.institute.Institute


@Dao
abstract class InstituteDao : BaseDao<Institute>() {
    @get:Query("SELECT * from tb_institutes")
    abstract val institutes: List<Institute>
}