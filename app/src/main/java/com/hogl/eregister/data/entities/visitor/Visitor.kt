package com.hogl.eregister.data.entities.visitor

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.RowId
import java.sql.Types.ROWID

@Entity(tableName = "tb_visitors")
data class Visitor(

    @PrimaryKey(autoGenerate = true)
    val vis_id: Long,
    @NonNull @ColumnInfo(name = "vis_first_name") val vis_first_name: String,
    @NonNull @ColumnInfo(name = "vis_last_name") val vis_last_name: String,
    @ColumnInfo(name = "vis_phone") val vis_phone: Int,
    @ColumnInfo(name = "vis_type") val vis_type: String,
    @ColumnInfo(name = "vis_IDNumber") val vis_IDNumber: String,
    @ColumnInfo(name = "vis_nfc_card")val vis_nfc_card:String,
    @ColumnInfo(name = "vis_qr_code")val vis_qr_code:String,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)
{
    fun toString(phoneId:String): String {
        return " { vis_id : $phoneId-$vis_id, vis_first_name :'$vis_first_name', vis_last_name :'$vis_last_name', vis_phone : $vis_phone, vis_type : '$vis_type', vis_IDNumber : '$vis_IDNumber',vis_nfc_card: '$vis_nfc_card', vis_qr_code: '$vis_qr_code' , timestamp : '$timestamp' }"
    }
}
