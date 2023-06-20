package com.hogl.eregister.data.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_groupMovements")
data class GroupMovement(

    @PrimaryKey(autoGenerate = true)
    @NonNull val grp_mv_id: Int,
    @ColumnInfo(name = "grp_id") val grp_id: Int,
    @ColumnInfo(name = "grp_mv_card") val grp_mv_card: String,
    @ColumnInfo(name = "grp_mv_type") val grp_mv_type: String,
    @ColumnInfo(name = "grp_mv_time") val grp_mv_time: Long,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
)
{
    fun toString(phoneId:String): String {
        return " { grp_mv_id : $phoneId-$grp_mv_id, grp_id :'$grp_id', grp_mv_card :'$grp_mv_card', grp_mv_type : '$grp_mv_type', grp_mv_time : '$grp_mv_time', timestamp : '$timestamp' }"
    }
}