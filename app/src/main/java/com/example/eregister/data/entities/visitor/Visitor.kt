package com.example.eregister.data.entities.visitor

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_visitors")
data class Visitor(
    @PrimaryKey
    @NonNull val vis_id: Int,
    @ColumnInfo(name = "vis_first_name") val vis_first_name: String,
    @ColumnInfo(name = "vis_last_name") val vis_last_name: String,
    @ColumnInfo(name = "vis_phone") val vis_phone: Int,
    @ColumnInfo(name = "vis_type") val vis_type: String,
    @ColumnInfo(name = "vis_IDNumber") val vis_IDNumber: Int
)
