package com.hogl.eregister.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.hogl.eregister.data.entities.visitor.Visitor
import kotlinx.coroutines.flow.Flow


@Dao
abstract class VisitorDao : BaseDao<Visitor>() {

    @Query("SELECT * from tb_visitors WHERE vis_id like :visId LIMIT 1")
    abstract fun findVisitorById(visId: Int): Flow<Visitor>

    @Query("SELECT * from tb_visitors WHERE vis_nfc_card like :tagId LIMIT 1")
    abstract fun findVisitorByNfc(tagId: String): Flow<Visitor>



    @Query("SELECT * from tb_visitors order by vis_first_name COLLATE NOCASE ASC")
    abstract fun allVisitors(): Flow<List<Visitor>>

    @Query("SELECT * from tb_visitors order by vis_first_name COLLATE NOCASE ASC")
    abstract fun testAllVisitors(): List<Visitor>

//
//    @Query("SELECT * from tb_visitors WHERE timestamp > :timestamp order by vis_first_name COLLATE NOCASE ASC")
//    abstract fun visitorToSync(timestamp: Long): Flow<List<Visitor>>


    @Query("SELECT * from tb_visitors order by vis_first_name COLLATE NOCASE ASC")
    abstract fun visitorToSync(): Flow<List<Visitor>>

    @Query("SELECT * FROM tb_visitors WHERE vis_first_name LIKE :vis_name OR vis_last_name LIKE :vis_name OR vis_type LIKE :vis_name order by vis_first_name COLLATE NOCASE ASC")
    abstract fun findVisitorByName(vis_name: String): Flow<List<Visitor>>


    @Query("SELECT * from tb_visitors WHERE vis_nfc_card like :tagId LIMIT 1")
    abstract fun findVisitorByTag(tagId: String): Flow<Visitor>

    //    update Visitor
    @Query("UPDATE tb_visitors SET vis_first_name = :vis_first_name, vis_last_name = :vis_last_name, vis_phone = :vis_phone, vis_type = :vis_type, vis_IDNumber = :vis_IDNumber, vis_nfc_card = :vis_nfc_card WHERE vis_id = :vis_id")
    abstract fun updateVisitor(vis_id: Int, vis_first_name: String, vis_last_name: String, vis_phone: Int, vis_type: String, vis_IDNumber: String, vis_nfc_card: String)

//    insert all visitor from a list of visitors
//    @Query("INSERT INTO tb_visitors (vis_id, vis_first_name, vis_last_name, vis_phone, vis_type, vis_IDNumber, vis_nfc_card) VALUES (:vis_id, :vis_first_name, :vis_last_name, :vis_phone, :vis_type, :vis_IDNumber, :vis_nfc_card)")
//    abstract override fun insertAll(visitors2: List<Visitor>)


//    @Query("SELECT * FROM tb_visitors WHERE vis_first_name LIKE :vis_name OR vis_last_name LIKE :vis_name")
//    abstract fun searchDatabase(vis_name: String): Flow<List<Visitor>>

}