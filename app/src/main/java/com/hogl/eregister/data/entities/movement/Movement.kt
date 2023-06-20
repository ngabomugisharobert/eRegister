package com.hogl.eregister.data.entities.movement

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tb_movements")
data class Movement(
    @PrimaryKey(autoGenerate = true) val mv_id: Long,
    @ColumnInfo(name = "visitor_id") val visitor_id: Int,
    @ColumnInfo(name = "gate_id") val gate_id: Int,
    @ColumnInfo(name = "mv_time") val mv_time: String,
    @ColumnInfo(name = "guard_id") val guard_id: Int,
    @ColumnInfo(name = "transportType") val transportType: String,
    @ColumnInfo(name = "vehicle_plate") val vehicle_plate: String,
    @ColumnInfo(name = "MovementType") val MovementType: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
)
{
    fun toString(android_id:String): String {
        return " { mv_id:$android_id-$mv_id, visitor_id:$android_id-$visitor_id, gate_id:$gate_id, mv_time:'$mv_time', guard_id:$guard_id, transportType:'$transportType', vehicle_plate:'$vehicle_plate', MovementType:'$MovementType', timestamp:'$timestamp'}"
    }
}
