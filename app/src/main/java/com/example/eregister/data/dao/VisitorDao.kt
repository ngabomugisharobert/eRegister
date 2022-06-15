package com.example.eregister.data.dao


import androidx.room.*
import com.example.eregister.data.visitor.Visitor

@Dao
interface VisitorDao {
    @Query("SELECT * FROM visitor")
    fun getAll(): List<Visitor>

    @Query("SELECT * FROM visitor WHERE vis_id IN (:visitorIds)")
    fun loadAllByIds(visitorIds: IntArray): List<Visitor>

    @Query("SELECT * FROM visitor WHERE vis_first_name LIKE :first AND " +
            "vis_last_name LIKE :last LIMIT 1")
    fun findByName(first: String, last: String): Visitor

    @Insert
    fun insertAll(vararg visitors: Visitor)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(visitor: Visitor)

    @Delete
    fun delete(user: Visitor)
}
