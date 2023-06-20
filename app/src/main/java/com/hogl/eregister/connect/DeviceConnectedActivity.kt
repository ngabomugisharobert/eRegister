package com.hogl.eregister.connect

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
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
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.gson.Gson
import com.hogl.eregister.R
import com.hogl.eregister.activities.HomeActivity
import com.hogl.eregister.connect.ServerClient.messagesChangedListener
import com.hogl.eregister.data.AppDatabase
import com.hogl.eregister.data.InitApplication
import com.hogl.eregister.data.entities.visitor.Visitor
import com.hogl.eregister.data.models.*
import com.hogl.eregister.utils.getFolder
import kotlinx.coroutines.*
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
    lateinit var phone_sync : LottieAnimationView

    lateinit var tag: String
    lateinit var handler: Handler
    var isHost by Delegates.notNull<Boolean>()
    var hostAddress: String = ""
    private lateinit var thisAct: DeviceConnectedActivity
    lateinit var thisContext: Context
    lateinit var deviceName: String
    lateinit var authStrings: ArrayList<String>
    var authStep by Delegates.notNull<Int>()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    lateinit var layout: View

    private val movementViewModel: MovementViewModel by viewModels {
        MovementViewModelFactory((this.application as InitApplication).movementRepository)
    }
    private val guardViewModel: GuardViewModel by viewModels {
        GuardViewModelFactory((this.application as InitApplication).guardRepository)
    }
    private val visitorViewModel: VisitorViewModel by viewModels {
        VisitorViewModelFactory((this.application as InitApplication).visitorRepository)
    }

    private val groupMovementViewModel: GroupMovementViewModel by viewModels {
        GroupMovementViewModelFactory((this.application as InitApplication).groupMovementRepository)
    }

    private  val GroupViewModel: GroupViewModel by viewModels {
        GroupViewModelFactory((this.application as InitApplication).groupRepository)
    }

    //override onPause to stop the serverClient
    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val applicationScope = CoroutineScope(SupervisorJob())
        val db by lazy { AppDatabase.getDatabase(this, applicationScope) }
        initComponents()
        val intent = intent
        isHost = intent.getBooleanExtra("isHost", false)
        hostAddress = intent.getStringExtra("hostAddress").toString()
        thisAct = this
        thisContext = applicationContext
        handler = Handler(this.getMainLooper())
        setUpServer(db)
        tag = if (isHost) "CHAT-HOST" else "CHAT-CLIENT"
    }

    interface finishedInterface {
        fun completed(k: SecretKey?): String?
    }

    var myInterface: finishedInterface = object : finishedInterface {
        override fun completed(k: SecretKey?): String? {
            Log.d("Auth", "AUTH SERVICE DONE!")
            serverClient.setMessagesChangedListener(object :
                messagesChangedListener {
            })
            serverClient.setSecured(true, null)
            protocolText.setBackgroundColor(resources.getColor(android.R.color.holo_green_light))
            return "Done"
        }
    }

    private fun setUpServer(db: AppDatabase) {
        var visitorFinished: Boolean = false
        var movementFinished: Boolean = false
        var groupFinished: Boolean = false
        var groupMovementFinished: Boolean = false
        var noData: Boolean = false

        //Phone is host
        if (isHost) {
            serverClient = ServerClient(authStrings, thisContext)
        } else {
            serverClient = ServerClient(hostAddress, authStrings, thisContext)
        }

        serverClient.start()

        //button event
        disconnectButton.setOnClickListener { disconnect() }

        sendButton.setOnClickListener {

            phone_sync.visibility = View.VISIBLE
            val database = JSONObject()

            val android_id: String = sharedPreferences.getString("android_id", "")!!
            val visitor_last_sync: String? =
                sharedPreferences.getString("visitor_last_sync", null)


            if (visitor_last_sync == null) {
                visitorViewModel.allVisitors.observe(this) { visitors ->
                    for (visitor in visitors) {
                        database.accumulate("visitors", JSONObject(visitor.toString(android_id)))
                    }
                    if (!visitors.isEmpty()) {
                        update_synchronize(
                            "visitor_last_sync",
                            System.currentTimeMillis().toString()
                        )
                    }
                    visitorFinished = true
                }
            }
            else {
                visitorViewModel.visitorToSync()
                    .observe(this) { visitors ->
                        for (visitor in visitors) {
                            database.accumulate("visitors", JSONObject(visitor.toString(android_id)))
                        }
                        if (!visitors.isEmpty()) {
                            update_synchronize(
                                "visitor_last_sync",
                                System.currentTimeMillis().toString()
                            )
                        }
                        visitorFinished = true
                    }
            }


            var group_last_sync: String? =
                sharedPreferences.getString("group_last_sync", null)

            if (group_last_sync == null) {
                GroupViewModel.allGroups.observe(this) { groups ->
                    for (group in groups) {
                        var grp = JSONObject(group.toString(android_id))
                        database.accumulate("groups", JSONObject(group.toString(android_id)))
                    }
                    if (!groups.isEmpty()) {
                        update_synchronize(
                            "group_last_sync",
                            System.currentTimeMillis().toString()
                        )
                    }
                    groupFinished = true
                }
            }
            else {
                GroupViewModel.groupToSync()
                    .observe(this) { groups ->
                        for (group in groups) {
                            val grp = JSONObject(group.toString(android_id))
                            database.accumulate("groups", grp)
                        }
                        if (!groups.isEmpty()) {
                            update_synchronize(
                                "group_last_sync",
                                System.currentTimeMillis().toString()
                            )
                        }
                        groupFinished = true
                    }
            }


            val groupMovement_last_sync: String? =
                sharedPreferences.getString("groupMovement_last_sync", null)

            if (groupMovement_last_sync == null) {
                groupMovementViewModel.allGroupMovements.observe(this) { groupMovements ->
                    for (groupMovement in groupMovements) {
                        database.accumulate(
                            "groupMovements",
                            JSONObject(groupMovement.toString(android_id))
                        )
                    }
                    if (!groupMovements.isEmpty()) {
                        update_synchronize(
                            "groupMovement_last_sync",
                            System.currentTimeMillis().toString()
                        )
                    }
                    groupMovementFinished = true
                }
            }
            else {
                groupMovementViewModel.groupMovementToSync()
                    .observe(this) { groupMovements ->
                        for (groupMovement in groupMovements) {
                            database.accumulate(
                                "groupMovements",
                                JSONObject(groupMovement.toString(android_id))
                            )
                        }
                        if (!groupMovements.isEmpty()) {
                            update_synchronize(
                                "groupMovement_last_sync",
                                System.currentTimeMillis().toString()
                            )
                        }
                        groupMovementFinished = true
                    }
            }

            val movement_last_sync: String? =
                sharedPreferences.getString("movement_last_sync", null)

            if (movement_last_sync == null) {

                movementViewModel.allMovements.observe(this) { movements ->
                    for (movement in movements) {
                        database.accumulate("movements", JSONObject(movement.toString(android_id)))
                    }
                    if (!movements.isEmpty()) {
                        update_synchronize(
                            "movement_last_sync",
                            System.currentTimeMillis().toString()
                        )
                    }
                    movementFinished = true
                }

            } else {
                movementViewModel.movementsToSync()
                    .observe(this) { movements ->
                        for (movement in movements) {
                            database.accumulate(
                                "movements",
                                JSONObject(movement.toString(android_id))
                            )
                        }
                        if (!movements.isEmpty()) {
                            update_synchronize(
                                "movement_last_sync",
                                System.currentTimeMillis().toString()
                            )
                        }
                        movementFinished = true
                    }
            }


            GlobalScope.launch {
                suspend {
                    Log.i("coroutineScope", "#runs on ${Thread.currentThread().name}")
                    delay(3000)
//test the value of this variable a
                    var a = db.visitorDao().testAllVisitors()

                    withContext(Dispatchers.Main) {

                        Log.i("coroutineScope", "#runs on ${Thread.currentThread().name}")

                        if (movementFinished && visitorFinished && groupFinished && groupMovementFinished) {
                            movementFinished = false
                            visitorFinished = false
                            groupFinished = false
                            groupMovementFinished = false
                            if (database.length() > 0) {
                                val jsonString = database.toString()
                                thisAct.saveJson(jsonString)
                            } else {
                                Toast.makeText(thisContext, "No data to sync", Toast.LENGTH_SHORT)
                                    .show()
                                phone_sync.visibility = View.INVISIBLE
                                noData = true
                                return@withContext
                            }
                            delay(4000)
                            if (!noData) {
                                noData = false
                                val executor = Executors.newSingleThreadExecutor()

                                executor.execute {
                                    if (true) {
                                        try {
                                            var `is`: InputStream? = null

                                            val directory = thisContext.getFolder()
                                            val dataFile = File(directory, "data.json")

                                            if (dataFile.exists()) {
                                                `is` = FileInputStream(dataFile)
                                                val cr = thisContext.contentResolver
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
                                                    System.arraycopy(
                                                        fileBytes,
                                                        offset,
                                                        data,
                                                        0,
                                                        size
                                                    )
                                                    offset += size
                                                    serverClient.write(data)
                                                }
                                                handler.post {
                                                    Toast.makeText(
                                                        thisContext,
                                                        "Synchronization finished",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                    phone_sync.visibility = View.INVISIBLE

                                                    var intent: Intent = Intent(
                                                        thisContext,
                                                        HomeActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                }
                                            } else {
                                                handler.post {
                                                    Toast.makeText(
                                                        thisContext,
                                                        "Synchronization Failed",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    phone_sync.visibility = View.INVISIBLE
                                                }
                                            }
                                        } catch (e: IOException) {
                                            e.printStackTrace()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }.invoke()
            }
        }
    }

    private fun initComponents() {
        protocolText = findViewById(R.id.protocol_text)
        sendButton = findViewById(R.id.send_button)
        disconnectButton = findViewById(R.id.disconnect_button)
        phone_sync = findViewById(R.id.phone_sync)
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
                        thisContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    //permission check
                    Toast.makeText(thisContext, "Permission not granted", Toast.LENGTH_SHORT)
                        .show()
                }
                manager.requestGroupInfo(
                    channel
                ) { group ->
                    try {
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
                    } catch (e: Exception) {
                        e.printStackTrace()
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