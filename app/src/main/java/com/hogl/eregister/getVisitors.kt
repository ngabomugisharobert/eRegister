package com.hogl.eregister

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.hogl.eregister.data.entities.visitor.Visitor

class GetVisitors() {
    fun getVisitors(firebase: FirebaseFirestore): List<VisitorClass> {
//        get all visitors from firebase collection tb_visitors
        val visitors = mutableListOf<VisitorClass>()
        firebase.collection("tb_visitors").get().addOnSuccessListener { result ->
            for (document in result) {
                Log.d(TAG, " what is this -> ${document.id} => ${document.data}")
                val visitor = VisitorClass(
                    0,
                    document.data["vis_first_name"].toString(),
                    document.data["vis_last_name"].toString(),
                    document.data["vis_phone"].toString().toInt(),
                    document.data["vis_type"].toString(),
                    document.data["vis_IDNumber"].toString(),
                    document.data["vis_nfc_card"].toString(),
                    document.data["vis_qr_code"].toString(),
                    document.data["time_stamp"].toString().toLong()
                )
                visitors.add(visitor)
            }
        }.addOnFailureListener { exception ->
            Log.d("TAG", "Error getting documents: ", exception)
        }
        return visitors
    }
}