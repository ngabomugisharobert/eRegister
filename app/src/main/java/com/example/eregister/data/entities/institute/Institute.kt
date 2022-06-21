package com.example.eregister.data.entities.institute

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_institutes")
data class Institute(

    @PrimaryKey
    @NonNull val inst_id: Int,
    @ColumnInfo(name = "inst_name") val inst_name: String,
    @ColumnInfo(name = "inst_address") val inst_address: String,
)
