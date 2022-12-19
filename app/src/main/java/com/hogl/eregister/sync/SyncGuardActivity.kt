package com.hogl.eregister.sync

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.hogl.eregister.data.InitApplication
import com.hogl.eregister.data.entities.guard.Guard
import com.hogl.eregister.data.entities.visitor.Visitor
import com.hogl.eregister.data.models.*
import com.hogl.eregister.databinding.ActivitySyncGuardBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class SyncGuardActivity : AppCompatActivity() {


    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var ref = this

    private lateinit var binding: ActivitySyncGuardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySyncGuardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.title = "Synchronize"
        initComponents()

        val visitorViewModel: VisitorViewModel by viewModels {
            VisitorViewModelFactory((application as InitApplication).visitorRepository)
        }

        val guardViewModel: GuardViewModel by viewModels {
            GuardViewModelFactory((application as InitApplication).guardRepository)
        }

        val file: File = File("data/data/com.hogl.eregister/databases/test2.json")
        if (!file.exists()) {
            Toast.makeText(this, "No such file", Toast.LENGTH_LONG).show()
            finish()
        } else {
            Toast.makeText(this, "synchronize...", Toast.LENGTH_LONG).show()

            val jsonString = JSONObject(file.readText())

            //get keys from jsonObject
            val keys = jsonString.keys()

            //check if keys exist
            while (keys.hasNext()) {
                val key = keys.next()
                var value = jsonString.get(key)
                when (key) {
                    "visitors" -> {
                        when (jsonString) {
                            is JSONObject -> {
                                // treat this as JsonObject}
                                val visitorObj = jsonString.getJSONObject("visitors")
                                val visitor: Visitor = Visitor(
                                    (visitorObj.getString("vis_id")).toLong(),
                                    visitorObj.getString("vis_first_name"),
                                    visitorObj.getString("vis_last_name"),
                                    visitorObj.getString("vis_phone").toInt(),
                                    visitorObj.getString("vis_type"),
                                    visitorObj.getString("vis_IDNumber"),
                                    visitorObj.getString("vis_nfc_card"),
                                    visitorObj.getString("vis_qr_code"),
                                    visitorObj.getString("timestamp").toLong()
                                )
                                visitorViewModel.insert(visitor)
                            }
                            is JSONArray -> {
                                //treat this as JsonArray
                                val visitors = jsonString.getJSONArray("visitors")
                                for (i in 0 until visitors.length()) {
                                    val objString = visitors.getJSONObject(i)
                                    val visitor: Visitor = Visitor(
                                        (objString.getString("vis_id")).toLong(),
                                        objString.getString("vis_first_name"),
                                        objString.getString("vis_last_name"),
                                        objString.getString("vis_phone").toInt(),
                                        objString.getString("vis_type"),
                                        objString.getString("vis_IDNumber"),
                                        objString.getString("vis_nfc_card"),
                                        objString.getString("vis_qr_code"),
                                        objString.getString("timestamp").toLong()
                                    )
                                    visitorViewModel.insert(visitor)
                                }
                            }
                            else -> { //I have to find some other way to handle this}
                                Toast.makeText(this, "Nothing to sync", Toast.LENGTH_LONG).show()
                            }
                        }


                    }
                    "guards" -> {
                        when (jsonString) {
                            is JSONObject -> {
                                // treat this as JsonObject}
                                val guardObj = jsonString.getJSONObject("guards")
                                val objString = guardObj
                                val guard: Guard = Guard(
                                    0,
                                    objString.getString("gua_first_name"),
                                    objString.getString("gua_last_name"),
                                    objString.getString("gua_username"),
                                    objString.getString("gua_password"),
                                    objString.getString("gua_email"),
                                    objString.getString("gua_phone").toInt(),
                                    objString.getString("gua_address"),
                                    objString.getString("inst_id").toInt(),
                                    objString.getString("gua_IDNumber"),
                                    objString.getString("timestamp").toLong()
                                )
                                guardViewModel.insert(guard)
                            }
                            is JSONArray -> {
                                //treat this as JsonArray
                                val guards = jsonString.getJSONArray("guards")
                                for (i in 0 until guards.length()) {
                                    val objString = guards.getJSONObject(i)
                                    val guard: Guard = Guard(
                                        0,
                                        objString.getString("guard_first_name"),
                                        objString.getString("guard_last_name"),
                                        objString.getString("gua_username"),
                                        objString.getString("gua_password"),
                                        objString.getString("gua_email"),
                                        objString.getString("gua_phone").toInt(),
                                        objString.getString("gua_address"),
                                        objString.getString("inst_id").toInt(),
                                        objString.getString("gua_IDNumber"),
                                        objString.getString("timestamp").toLong()
                                    )
                                    guardViewModel.insert(guard)
                                }
                            }
                            else -> { //I have to find some other way to handle this}
                                Toast.makeText(this, "Nothing to sync", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                    else -> {
                        Toast.makeText(this, "Nothing to sync", Toast.LENGTH_LONG).show()
                    }
                }
            }

            // toast a message to show that the synchronization is complete
            Toast.makeText(this, "Synchronization complete", Toast.LENGTH_LONG).show()
            finish()
        }
        Toast.makeText(
            ref,
            "last_sync done",
            Toast.LENGTH_LONG
        ).show()


    }

    //TODO string to add in values
    private fun initComponents() {

        sharedPreferences =
            applicationContext.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

    }
}