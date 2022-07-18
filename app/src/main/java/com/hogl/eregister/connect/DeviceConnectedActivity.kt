package com.hogl.eregister.connect

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.hogl.eregister.R
import com.hogl.eregister.activities.HomeActivity
import com.hogl.eregister.connect.ServerClient.messagesChangedListener
import com.hogl.eregister.data.InitApplication
import com.hogl.eregister.data.models.*
import com.hogl.eregister.utils.getFolder
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors
import javax.crypto.SecretKey
import kotlin.properties.Delegates

class DeviceConnectedActivity : AppCompatActivity() {
    lateinit var protocolText: TextView
    lateinit var sendButton: Button
    lateinit var serverClient: ServerClient
    lateinit var disconnectButton: Button
    lateinit var tag: String
    lateinit var handler: Handler
    var isHost by Delegates.notNull<Boolean>()
    var hostAddress: String = ""
    lateinit var thisAct: Activity
    lateinit var thisContext: Context
    lateinit var deviceName: String
    lateinit var authStrings: ArrayList<String>
    var authStep by Delegates.notNull<Int>()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private val movementViewModel: MovementViewModel by viewModels {
        MovementViewModelFactory((this.application as InitApplication).movementRepository)
    }
    private val guardViewModel: GuardViewModel by viewModels {
        GuardViewModelFactory((this.application as InitApplication).guardRepository)
    }
    private val visitorViewModel: VisitorViewModel by viewModels {
        VisitorViewModelFactory((this.application as InitApplication).visitorRepository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initComponents()

        val intent = intent
        isHost = intent.getBooleanExtra("isHost", false)
        hostAddress = intent.getStringExtra("hostAddress").toString()
        thisAct = this
        thisContext = applicationContext
        handler = Handler(this.getMainLooper())
        setUpServer()
        tag = if (isHost!!) "CHAT-HOST" else "CHAT-CLIENT"
    }

    interface finishedInterface {
        fun completed(k: SecretKey?): String?
    }

    var myInterface: finishedInterface = object : finishedInterface {
        override fun completed(k: SecretKey?): String? {
            Log.d("Auth", "AUTH SERVICE DONE!")
            serverClient!!.setMessagesChangedListener(object :
                messagesChangedListener { //            @Override
                //            public void onMessagesChangedListener() {
                //                runOnUiThread(new Runnable() {
                //                    @Override
                //                    public void run() {
                //                        adapter.notifyDataSetChanged();
                //                        messagesView.smoothScrollToPosition(adapter.getItemCount());
                //                    }
                //                });
                //            }
            })
            serverClient!!.setSecured(true, null)
            sendButton!!.isEnabled = true
            protocolText!!.text = "SECURED"
            protocolText!!.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
            return "Done"
        }
    }

    private fun setUpServer() {
        //Phone is host
        if (isHost!!) {
            serverClient = ServerClient(authStrings, thisContext)
        } else {
            serverClient = ServerClient(hostAddress, authStrings, thisContext)
        }
        serverClient!!.start()
        disconnectButton!!.setOnClickListener { disconnect() }

        sendButton!!.setOnClickListener {

            var database = JSONObject()

            var visitor_last_sync: String? =
                sharedPreferences.getString("visitor_last_sync", null)

            if (visitor_last_sync == null) {
                visitorViewModel.allVisitors.observe(this) { visitors ->
                    for (visitor in visitors) {
                        database.accumulate("visitors", JSONObject(visitor.toString()))
                    }
                    update_synchronize("visitor_last_sync", System.currentTimeMillis().toString())
                }
            } else {
                visitorViewModel.visitorToSync(visitor_last_sync.toString())
                    .observe(this) { visitors ->
                        for (visitor in visitors) {
                            database.accumulate("visitors", JSONObject(visitor.toString()))
                        }
                    }

                update_synchronize("visitor_last_sync", System.currentTimeMillis().toString())
            }


            var movement_last_sync: String? =
                sharedPreferences.getString("movement_last_sync", null)

            if (movement_last_sync == null) {

                movementViewModel.allMovements.observe(this) { movements ->
                    for (movement in movements) {
                        database.accumulate("movements", JSONObject(movement.toString()))
                    }
                    update_synchronize("movement_last_sync", System.currentTimeMillis().toString())
                }

            } else {
                movementViewModel.movementsToSync(movement_last_sync).observe(this) { movements ->
                    for (movement in movements) {
                        database.accumulate("movements", JSONObject(movement.toString()))
                    }
                }
                update_synchronize("movement_last_sync", System.currentTimeMillis().toString())
            }

            if (database.length() > 0) {
                val jsonString = database.toString()
                this.saveJson(jsonString)
            } else {
                Toast.makeText(thisContext, "No data to sync", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val executor = Executors.newSingleThreadExecutor()

            executor.execute {
                if (true) {
                    try {
                        var `is`: InputStream? = null

                        var directory = thisContext.getFolder()
                        var dataFile = File(directory, "data.json")

                        if (dataFile.exists()) {
                            `is` = FileInputStream(dataFile)
                            val cr = thisContext!!.contentResolver
                            val fileBytes = ByteArray(`is`.available())
                            `is`.read(fileBytes)
                            `is`.close()
                            var offset = 0
                            val AttributeDataLen = 244
                            val testLength = fileBytes.size.toString() + ""
                            val siz =
                                testLength.toByteArray(StandardCharsets.UTF_8)
                            serverClient!!.write(siz)
                            while (offset < fileBytes.size) {
                                var size = fileBytes.size - offset
                                if (size > AttributeDataLen) {
                                    size = AttributeDataLen
                                }
                                val data = ByteArray(size)
                                System.arraycopy(fileBytes, offset, data, 0, size)
                                offset += size
                                serverClient!!.write(data)
                            }
                            handler!!.post {
                                Toast.makeText(
                                    thisContext,
                                    "Synchronization finished",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            handler!!.post {
                                Toast.makeText(
                                    thisContext,
                                    "Synchronization Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }


            }
        }
    }

    private fun initComponents() {
        protocolText = findViewById(R.id.protocol_text)
        sendButton = findViewById(R.id.send_button)
        disconnectButton = findViewById(R.id.disconnect_button)
        deviceName = Settings.Global.getString(contentResolver, "device_name")
        authStrings = ArrayList()
        authStep = 0

        sharedPreferences =
            applicationContext.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    private fun saveJson(s: String) {
        val output: Writer

        var directory = thisContext.getFolder()
        var dataFile = File(directory, "data.json")


        val file = File("data/data/com.hogl.eregister/databases/database.json")
        if (!dataFile.exists()) {
            dataFile.createNewFile()
        }
        output = BufferedWriter(FileWriter(dataFile))
        output.write(s)
        output.close()
    }

    private fun disconnect() {
        Executors.newSingleThreadExecutor().execute {
            val manager =
                getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
            val channel = manager.initialize(
                thisContext,
                mainLooper, null
            )
            if (channel != null) {
                if (ActivityCompat.checkSelfPermission(
                        thisContext!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    //permission check
                }
                manager.requestGroupInfo(
                    channel
                ) { group ->
                    if (group != null) {
                        manager.removeGroup(channel, object : WifiP2pManager.ActionListener {
                            override fun onSuccess() {
                                Log.d("p2p-disconnect", "removeGroup onSuccess -")
                                triggerRebirth(thisContext)
                            }

                            override fun onFailure(reason: Int) {
                                Log.d(
                                    "p2p-disconnect",
                                    "removeGroup onFailure -$reason"
                                )
                                triggerRebirth(thisContext)
                            }
                        })
                    } else {
                        triggerRebirth(thisContext)
                    }
                }
            } else {
                Log.d("DISCONNECT", "channel is null")
            }
        }
    }

    private fun update_synchronize(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    companion object {
        fun triggerRebirth(context: Context?) {
            val intent = Intent(context, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(intent)
            if (context is Activity) {
                context.finish()
            }
            Runtime.getRuntime().exit(0)
        }
    }
}