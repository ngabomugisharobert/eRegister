package com.hogl.eregister.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.hogl.eregister.data.entities.GroupMovement
import com.hogl.eregister.data.entities.visitor.Visitor
import kotlinx.coroutines.flow.Flow

@Dao
abstract class GroupMovementDao :BaseDao<GroupMovement>() {

    @Query("SELECT * from tb_groupMovements")
    abstract fun allGroupMovements(): Flow<List<GroupMovement>>

    @Query("SELECT * from tb_groupMovements where grp_mv_time > :timestamp")
    abstract fun groupMovementsToSync(timestamp: Long): Flow<List<GroupMovement>>

    @Query("SELECT * from tb_groupMovements where grp_id like :grpId")
    abstract fun getGRoupMovementsByGroupId(grpId: String): Flow<List<GroupMovement>>

    @Query("SELECT * from tb_groupMovements where grp_id LIKE :grpId AND grp_mv_type LIKE :s" )
    abstract fun getGRoupMovementsByGroupIdType(grpId: Int, s: String): Flow<List<GroupMovement>>


}