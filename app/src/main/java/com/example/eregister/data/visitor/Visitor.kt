package com.example.eregister.data.visitor

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Visitor(
    @PrimaryKey val vis_id: Int,
    @ColumnInfo(name = "vis_first_name") val vis_firstName: String,
    @ColumnInfo(name = "vis_last_name") val vis_lastName: String,
    @ColumnInfo(name = "vis_phone") val vis_phone: Int,
    @ColumnInfo(name = "vis_type") val vis_type: String,
    @ColumnInfo(name = "vis_IDNumber") val vis_IDNumber: Int
)
