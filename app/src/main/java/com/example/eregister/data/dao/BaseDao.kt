package com.example.eregister.data.dao

import androidx.room.*
import com.example.eregister.data.entities.visitor.Visitor

@Dao
abstract class BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(entity: T)
    @Update
    abstract fun update(entity: T)
    @Delete
    abstract fun delete(entity: T)

}
