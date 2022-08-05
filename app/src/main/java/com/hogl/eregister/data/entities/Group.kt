package com.hogl.eregister.data.entities

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_groups")
data class Group(

    @PrimaryKey(autoGenerate = true)
    @NonNull val grp_id: Int,
    @ColumnInfo(name = "grp_name") val grp_name: String,
    @ColumnInfo(name = "grp_leader") val grp_leader: String,
    @ColumnInfo(name = "grp_members") val grp_members: Int,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
        )