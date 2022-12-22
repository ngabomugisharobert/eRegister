package com.hogl.eregister

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.hogl.eregister.activities.HomeActivity
import com.hogl.eregister.data.InitApplication
import com.hogl.eregister.data.models.GuardViewModel
import com.hogl.eregister.data.models.GuardViewModelFactory
import com.hogl.eregister.databinding.ActivityLoginBinding
import com.hogl.eregister.lifecycle.MainActivityObserver
import com.hogl.eregister.utils.SessionManagement
import java.util.*
import kotlin.concurrent.schedule


class LoginActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private lateinit var txtUsername: TextView
    private lateinit var txtPassword: TextView
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var btnForgotPassword: Button
    private lateinit var loginProgressBar: ProgressBar
    private lateinit var container: ConstraintLayout

    private lateinit var asset_form: LinearLayout
    private lateinit var loading_form: LottieAnimationView
    private lateinit var loading_form_no_internet: LottieAnimationView

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView: View

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var selected: Int = -1
    private var selectedAsset = ""
    private var assetsPassword = ""

    private val gates = arrayOf("Gate 1", "Gate 2", "Gate 3", "Gate 4")
    private var assets = mutableListOf("Choose Asset")
    private var isLoginSuccess: Boolean = false

    private lateinit var android_id: String

    private val guardViewModel: GuardViewModel by viewModels {
        GuardViewModelFactory((application as InitApplication).guardRepository)
    }

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()


        if (!checkInit()) {

            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.setCancelable(false)
            bottomSheetDialog.show()

            val networkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build()

            val networkCallback = object : ConnectivityManager.NetworkCallback() {
                // network is available for use
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    runOnUiThread {
                        loading_form.visibility = View.VISIBLE
                        asset_form.visibility = View.INVISIBLE
                        loading_form_no_internet.visibility = View.GONE
                        assetFormLoad()
                    }
                }


                // Network capabilities have changed for the network
                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    val unmetered =
                        networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
                }

                // lost network connection
                override fun onLost(network: Network) {
                    super.onLost(network)
                    runOnUiThread {
                        asset_form.visibility = View.INVISIBLE
                        loading_form_no_internet.visibility = View.VISIBLE
                    }
                }
            }
            val connectivityManager =
                getSystemService(ConnectivityManager::class.java) as ConnectivityManager
            connectivityManager.requestNetwork(networkRequest, networkCallback)
            if (!checkInternet(this)) {
                loading_form_no_internet.visibility = View.VISIBLE
                loading_form.visibility = View.GONE
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            } else {
                assetFormLoad()
            }
        } else {
            btnLogin.isEnabled = true
        }
    }


    override fun onStart() {
        super.onStart()
        if (checkInit()) {
            checkSession()
        }
    }

    private fun checkInit(): Boolean {
        // check if app is initialized from shared preference
        val asset_name = sharedPreferences.getString("asset_name", "")
        //return true if app is initialized
        return asset_name != ""
    }

    private fun checkInternet(context: Context): Boolean {
        val cnnct = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = cnnct.activeNetwork ?: return false
            val activeNetwork = cnnct.getNetworkCapabilities(network) ?: return false

            return when {
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                else -> false
            }
        } else {
            @Suppress("DEPRECATION") val networkInfo = cnnct.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }

    private fun assetFormLoad() {
        val db = Firebase.firestore
        db.collection("tb_assets")
            .get()
            .addOnSuccessListener { result ->
                loading_form.visibility = View.GONE
                asset_form.visibility = View.VISIBLE
                for (document in result) {
                    assets.add(document.data.get("asset_name").toString())
                }
                var arrAsset = Array<String>(assets.size, { i -> assets[i] })
                Log.d(TAG, "onComplete : ${arrAsset.size}")


                var assetSpinner = bottomSheetView.findViewById<Spinner>(R.id.asset_spinner)
                assetSpinner.adapter =
                    ArrayAdapter(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        arrAsset.distinct()
                    )
                assetSpinner.onItemSelectedListener = this

                var btnStart =
                    bottomSheetView.findViewById<Button>(R.id.btn_start)
                        .setOnClickListener {
                            assetsPassword =
                                bottomSheetView.findViewById<EditText>(R.id.asset_password).text.toString()
                            if (!assetsPassword.isEmpty() && selectedAsset != "Choose Asset" && selectedAsset != "") {
                                // compare with database
                                val db = Firebase.firestore
                                db.collection("tb_assets")
                                    .whereEqualTo("asset_name", selectedAsset)
                                    .whereEqualTo("password", assetsPassword)
                                    .get()
                                    .addOnSuccessListener { result ->
                                        if (!result.isEmpty) {
                                            // save to shared preference
                                            editor.putString("asset_name", selectedAsset)
                                            //get time from firebase
//                                                editor.putString("starting_time", )
                                            editor.apply()
                                            // Save this phone with asset name to firebase
                                            val phone = hashMapOf(
                                                "Phone_ID" to android_id,
                                                "Asset_ID" to result.first().id,
                                                "First_login" to FieldValue.serverTimestamp()
                                            )

// Add a new document with a generated ID
                                            db.collection("tb_phones")
                                                .add(phone)
                                                .addOnSuccessListener { documentReference ->
                                                    Log.d(
                                                        TAG,
                                                        "DocumentSnapshot added with ID: ${documentReference.id}"
                                                    )
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w(TAG, "Error adding document", e)
                                                }
                                            bottomSheetDialog.dismiss()
                                        } else {
                                            Toast.makeText(
                                                this,
                                                "Wrong password",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.w(TAG, "Error getting documents.", exception)
                                    }
                            } else {
                                Toast.makeText(
                                    this,
                                    "Please select asset and enter password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }

    private fun checkSession() {
        //check if user is logged in
        //if user is logged in --> move to Home
        val sessionManagement = SessionManagement(this@LoginActivity)
        val userID: Int = sessionManagement.session
        if (userID != -1) {

            //user id logged in and so move to Home
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        } else {
            //user is not logged in
            //do nothing
//            btnLogin.setOnClickListener {
//                guardViewModel.checkLogin(
//                    txtUsername.text.toString(),
//                    txtPassword.text.toString()
//                ).observe(this) {
//                    if (it != null) {
//                        Toast.makeText(
//                            this@LoginActivity,
//                            R.string.loginSuccess,
//                            Toast.LENGTH_SHORT
//                        ).show()
//
//                        isLoginSuccess = true
//
//                        val mAlertDialogBuilder = AlertDialog.Builder(this@LoginActivity)
//                        mAlertDialogBuilder.setTitle(R.string.selectGate)
//                        mAlertDialogBuilder.setCancelable(false)
//                        mAlertDialogBuilder.setSingleChoiceItems(
//                            gates,
//                            -1
//                        ) { dialog, which ->
//                            when (which) {
//                                which -> {
//                                    selected = which + 1
//                                    Toast.makeText(
//                                        this@LoginActivity,
//                                        "You selected: " + gates[which],
//                                        Toast.LENGTH_SHORT
//                                    ).show()
//                                }
//                            }
//                        }
//
//                        mAlertDialogBuilder.setPositiveButton(R.string.select) { _, _ ->
//                            val android_id :String = Settings.Secure.getString(
//                                this.contentResolver,
//                                Settings.Secure.ANDROID_ID
//                            )
//                            val user =
//                                User(
//                                    it.gua_id,
//                                    it.gua_first_name,
//                                    it.gua_last_name,
//                                    it.gua_username,
//                                    selected,
//                                    android_id
//                                )
//                            editor.putString("android_id", android_id)
//                            editor.apply()
//                            val sessionManagement: SessionManagement =
//                                SessionManagement(this@LoginActivity)
//                            sessionManagement.saveSession(user)
//                            val userID: Int = sessionManagement.session
//                            if (userID != -1) {
//
//                                var gson = Gson().toJson(user)
//                                editor.putString("USER", gson)
//                                editor.apply()
//                                this.moveToHome()
//                                finish()
//                            }
//                        }
//                        val mAlertDialog = mAlertDialogBuilder.create()
//                        mAlertDialog.show()
//
//
//                    } else {
//                        loginProgressBar.visibility = ProgressBar.VISIBLE
//                        Timer().schedule(1000) {
//                            loginProgressBar.visibility = ProgressBar.INVISIBLE
//                            runOnUiThread {
//                                Toast.makeText(
//                                    this@LoginActivity,
//                                    R.string.invalidUsernamePassword,
//                                    Toast.LENGTH_SHORT
//                                ).show()
//                            }
//                        }
//
//                    }
//                }
//            }
        }
    }

    override fun onBackPressed() {
        //do nothing
    }

    fun fnbtnLogin(view: View) {
        guardViewModel.checkLogin(
//            remove spaces and convert to lowercase username
            txtUsername.text.toString().replace("\\s".toRegex(), "").lowercase(Locale.getDefault()),
            txtPassword.text.toString()
        ).observe(this) {
            if (it != null) {
                Toast.makeText(
                    this@LoginActivity,
                    R.string.loginSuccess,
                    Toast.LENGTH_SHORT
                ).show()

                isLoginSuccess = true

                val mAlertDialogBuilder = AlertDialog.Builder(this@LoginActivity)
                mAlertDialogBuilder.setTitle(R.string.selectGate)
                mAlertDialogBuilder.setCancelable(false)
                mAlertDialogBuilder.setSingleChoiceItems(
                    gates,
                    -1
                ) { dialog, which ->
                    when (which) {
                        which -> {
                            selected = which + 1
                            Toast.makeText(
                                this@LoginActivity,
                                "You selected: " + gates[which],
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                mAlertDialogBuilder.setPositiveButton(R.string.select) { _, _ ->

                    val user =
                        User(
                            it.gua_id,
                            it.gua_first_name,
                            it.gua_last_name,
                            it.gua_username,
                            selected,
                            android_id
                        )
                    editor.putString("android_id", android_id)
                    editor.apply()
                    val sessionManagement: SessionManagement =
                        SessionManagement(this@LoginActivity)
                    sessionManagement.saveSession(user)
                    val userID: Int = sessionManagement.session
                    if (userID != -1) {

                        var gson = Gson().toJson(user)
                        editor.putString("USER", gson)
                        editor.apply()
                        this.moveToHome()
                        finish()
                    }
                }
                val mAlertDialog = mAlertDialogBuilder.create()
                mAlertDialog.show()


            } else {
                loginProgressBar.visibility = ProgressBar.VISIBLE
                Timer().schedule(1000) {
                    loginProgressBar.visibility = ProgressBar.INVISIBLE
                    runOnUiThread {
                        Toast.makeText(
                            this@LoginActivity,
                            R.string.invalidUsernamePassword,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }
        }
    }

    private fun moveToHome() {
        var gson: Gson = Gson()
        var data: String? = sharedPreferences.getString("USER", null)
        if (data != null) {
            var user: User = gson.fromJson(data, User::class.java)
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun initComponents() {
        android_id = Settings.Secure.getString(
            this.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        sharedPreferences =
            applicationContext.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        bottomSheetView =
            layoutInflater.inflate(
                R.layout.after_install,
                findViewById(R.id.bottom_sheet)
            )
        asset_form = bottomSheetView.findViewById(R.id.asset_form)
        loading_form = bottomSheetView.findViewById(R.id.loading_form)
        loading_form_no_internet = bottomSheetView.findViewById(R.id.loading_form_no_internet)

        txtUsername = binding.txtUsername
        txtPassword = binding.txtPassword
        btnLogin = binding.btnLogin
        loginProgressBar = binding.loginProgressBar
        container = binding.container
        lifecycle.addObserver(MainActivityObserver())



        if (isDarkThemeOn()) {
            container.background = resources.getDrawable(R.drawable.bg2, theme)
        } else {
            container.background = resources.getDrawable(R.drawable.bg23, theme)
        }

    }


    private fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    companion object {
        private val TAG: String = LoginActivity::class.java.simpleName
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedAsset = assets[position]
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        Toast.makeText(this, "You selected none ", Toast.LENGTH_SHORT).show()
    }


}