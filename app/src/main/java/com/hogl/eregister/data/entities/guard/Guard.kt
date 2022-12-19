package com.hogl.eregister.data.entities.guard

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tb_guards", indices = [
    Index(
        value = ["gua_first_name", "gua_last_name" ],
        unique = true
    )
])
data class Guard(

    @PrimaryKey(autoGenerate = true)
    val gua_id: Int,
    @ColumnInfo(name = "gua_first_name") val gua_first_name: String,
    @ColumnInfo(name = "gua_last_name") val gua_last_name: String,
    @ColumnInfo(name = "gua_username") val gua_username: String,
    @ColumnInfo(name = "gua_password") val gua_password: String,
    @ColumnInfo(name = "gua_email") val gua_email: String,
    @ColumnInfo(name = "gua_phone") val gua_phone: Int,
    @ColumnInfo(name = "gua_address") val gua_address: String,
    @ColumnInfo(name = "inst_id") val inst_id: Int,
    @ColumnInfo(name = "gua_IDNumber") val gua_IDNumber: String,
    @ColumnInfo(name = "timestamp") val timestamp: Long,
        )