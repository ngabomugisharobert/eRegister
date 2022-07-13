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
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors
import javax.crypto.SecretKey
import kotlin.properties.Delegates

class ChatActivity : AppCompatActivity() {
    lateinit var statusText: TextView
    lateinit var protocolText: TextView
    lateinit var messagesView: RecyclerView
    lateinit var sendButton: Button
    lateinit var serverClient: ServerClient
    lateinit var messageLayout: LinearLayout
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
        val intent = intent
        isHost = intent.getBooleanExtra("isHost", false)
        hostAddress = intent.getStringExtra("hostAddress").toString()
        thisAct = this
        thisContext = applicationContext
        handler = Handler(this.getMainLooper())
        initComponents()
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
            statusText!!.text = "Host"
            serverClient = ServerClient(authStrings, thisContext)
        } else {
            statusText!!.text = "Client"
            serverClient = ServerClient(hostAddress, authStrings, thisContext)
        }
        serverClient!!.start()
        disconnectButton!!.setOnClickListener { disconnect() }

        sendButton!!.setOnClickListener {

            var database = JSONObject()
            visitorViewModel.allVisitors.observe(this) { visitors ->
                for (visitor in visitors) {
                    val visitorJson = Gson().toJson(visitor)
                    database.accumulate("visitors", JSONObject(visitor.toString()))
                }
            }
            movementViewModel.allMovements.observe(this) { movements ->
                for (movement in movements) {
                    val movementJson = Gson().toJson(movement)
                    database.accumulate("movements", JSONObject(movement.toString()))
                }
            }
            guardViewModel.allGuards.observe(this) { guards ->
                for (guard in guards) {
                    val guardJson = Gson().toJson(guard)
                    database.accumulate("guards", guardJson)
                }
            }
            if(database.length() > 0) {
                val jsonString = database.toString()
               this.saveJson(jsonString)
            }
            Thread.sleep(1000)
            val executor = Executors.newSingleThreadExecutor()

            executor.execute {
                if (true) {
                    try {
                        var `is`: InputStream? = null
                        val fi =
                            File("data/data/com.hogl.eregister/databases/database.json")
                        if (fi.exists()) {
                            Log.e("&&&&&", "File exists")
                        }
                        `is` = FileInputStream(fi)
                        val cr = thisContext!!.contentResolver
                        //                                InputStream inputStream = cr.openInputStream(Uri.parse("data/data/"+thisContext.getPackageName()+ "/databases/eRegister_database"));
                        //                                is = getAssets().open("transfer_database");
                        val fileBytes = ByteArray(`is`.available())
                        `is`.read(fileBytes)
                        `is`.close()
                        var offset = 0
                        val AttributeDataLen = 244
                        //
                        //                                String encryptedMessage = protocolUtils.encrypt((String.valueOf(fileBytes.length).getBytes(StandardCharsets.UTF_8)), sharedKey);
                        Log.d(
                            tag + "size of byte array : ",
                            fileBytes.size.toString()
                        )
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

                            //                                    encryptedMessage = protocolUtils.encrypt(data, sharedKey);
                            Log.d("$tag-SENT", data.toString())
                            serverClient!!.write(data)
                        }
                        handler!!.post {
                            Toast.makeText(
                                thisContext,
                                "Synchronize finished",
                                Toast.LENGTH_SHORT
                            ).show()
//                            val intent = Intent(thisAct, HomeActivity::class.java)
//                            startActivity(intent)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }


            }

            //Hides the keyboard
            //                InputMethodManager imm = (InputMethodManager) thisAct.getSystemService(Activity.INPUT_METHOD_SERVICE);
            //                View v = thisAct.getCurrentFocus();
            //                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private fun initComponents() {
        statusText = findViewById(R.id.status_text)
        protocolText = findViewById(R.id.protocol_text)
        sendButton = findViewById(R.id.send_button)
        disconnectButton = findViewById(R.id.disconnect_button)
        deviceName = Settings.Global.getString(contentResolver, "device_name")
        authStrings = ArrayList()
        authStep = 0
    }

    private fun saveJson(s: String) {
        val output:Writer

        val file = File("data/data/com.hogl.eregister/databases/database.json")
        if (!file.exists()) {
            file.createNewFile()
        }
        output=BufferedWriter(FileWriter(file))
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