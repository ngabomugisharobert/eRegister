package com.hogl.eregister.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.provider.Settings.Secure
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.drawerlayout.widget.DrawerLayout
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.hogl.eregister.LoginActivity
import com.hogl.eregister.R
import com.hogl.eregister.User
import com.hogl.eregister.connect.MainActivity
import com.hogl.eregister.data.InitApplication
import com.hogl.eregister.data.entities.visitor.Visitor
import com.hogl.eregister.data.models.VisitorViewModel
import com.hogl.eregister.data.models.VisitorViewModelFactory
import com.hogl.eregister.databinding.ActivityHomeBinding
import com.hogl.eregister.extensions.TagExtension.getTagId
import com.hogl.eregister.utils.*
import java.security.AccessController.getContext
import java.util.*
import kotlin.concurrent.schedule


class HomeActivity : AppCompatActivity() {

    private val visitorViewModel: VisitorViewModel by viewModels {
        VisitorViewModelFactory((this.application as InitApplication).visitorRepository)
    }

    private lateinit var user: User
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var txtWelcome: TextView
    private lateinit var btnRegistered: CardView
    private lateinit var btnNewVisitor: CardView
    private lateinit var btnSync: CardView
    private lateinit var navDrawertoggleButton: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var nav_layout: NavigationView
    private lateinit var container: ConstraintLayout
    private lateinit var btnScan: CardView
    private lateinit var btnRFID: CardView
    private lateinit var btnGroup: CardView

    private lateinit var nfcScanLoading: LottieAnimationView


    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        onClickListeners()
        if (!this.nfcActivation()) btnRFID.visibility = View.GONE

    }


    private fun onClickListeners() {
        btnScan.setOnClickListener {
            val intent1 = Intent(applicationContext, ScanActivity::class.java)
            startActivity(intent1)
        }


        btnRegistered.setOnClickListener {
            val intent = Intent(this, RegisteredVisitorActivity::class.java)
            startActivity(intent)
        }

        var resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    val vis_first_name =
                        data?.getStringExtra(NewVisitorActivity.VIS_FIRST_NAME).toString()
                    val vis_last_name =
                        data?.getStringExtra(NewVisitorActivity.VIS_LAST_NAME).toString()
                    val vis_type = data?.getStringExtra(NewVisitorActivity.VIS_TYPE).toString()
                    val vis_phone = data?.getStringExtra(NewVisitorActivity.VIS_PHONE).toString()
                    val vis_idNumber =
                        data?.getStringExtra(NewVisitorActivity.VIS_ID_NUMBER).toString()
                    val vis_nfc_card =
                        data?.getStringExtra(NewVisitorActivity.VIS_NFC_CARD).toString()

//                    generate a random number for the visitor's qr code using generateRandomNumber() function
                    val vis_qr_code = generateRandomNumber()

                    val visitor = Visitor(
                        0,
                        vis_first_name,
                        vis_last_name,
                        vis_phone.toInt(),
                        vis_type,
                        vis_idNumber,
                        vis_nfc_card,
                        "001"+vis_qr_code,
                        System.currentTimeMillis()
                    )
//                    TODO NFC ID CArd and QR CODE TO BE Implemented
                    visitorViewModel.insert(visitor)
                    Log.e("VISITOR", Gson().toJson(visitor))
                    Toast.makeText(applicationContext, R.string.saved, Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        R.string.visitorNotSaved,
                        Toast.LENGTH_LONG
                    ).show()

                }
            }

        btnNewVisitor.setOnClickListener {
            val intent = Intent(this, NewVisitorActivity::class.java)
            resultLauncher.launch(intent)
        }

        btnSync.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        btnRFID.setOnClickListener {
            Timer("SettingUp", false).schedule(3000) {
                nfcScanLoading.bringToFront()
                nfcScanLoading.visibility = View.VISIBLE
            }
            nfcScanLoading.visibility = View.GONE
        }

        btnGroup.setOnClickListener {
            val intent = Intent(this, GroupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun generateRandomNumber(): Int {
        val random = Random()
        val number = random.nextInt(999999999)
        return number
    }

    private fun initComponents() {
        this.title = ""
        btnRegistered = binding.crdRegistered
        btnNewVisitor = binding.crdNewVisitor
        btnSync = binding.crdSync
        btnScan = binding.crdQRcode
        btnGroup = binding.crdGroup
        btnRFID = binding.crdRFID
        drawerLayout = binding.drawerLayout
        nav_layout = binding.navView
        container = binding.container
        nfcScanLoading = binding.nfcScanLoading

        if (isDarkThemeOn()) {
            container.background = resources.getDrawable(R.drawable.bg2, theme)
        } else {
            container.background = resources.getDrawable(R.drawable.bg23, theme)
        }

        var zero: Any = 0f
        supportActionBar?.elevation = zero as Float
        navDrawertoggleButton =
            ActionBarDrawerToggle(this, drawerLayout, R.string.saved, R.string.allField)
        drawerLayout.addDrawerListener(navDrawertoggleButton)
        navDrawertoggleButton.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        nav_layout.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item1 -> {
                    Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
                    val sessionManagement: SessionManagement =
                        SessionManagement(this)
                    sessionManagement.removeSession()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }

                R.id.item2 -> {
//                    Toast.makeText(
//                        this,
//                        "Change Password to be implemented",
//                        Toast.LENGTH_SHORT
//                    ).show()

                    val inflate = layoutInflater
                    val inflater = inflate.inflate(R.layout.loading_toast, null)

                    val progressBar = inflater.findViewById(R.id.progressBar) as ProgressBar

                    val alert = AlertDialog.Builder(this)
                    alert.setView(inflater)

                    alert.setCancelable(false)

                    val alertDialog = alert.create()
                    alertDialog.show()
                }
            }
            true
        }
        var test: String = ""
        //welcome Message
        txtWelcome = binding.txtWelcome
        sharedPreferences =
            applicationContext.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        var gson: Gson = Gson()
        var data: String? = sharedPreferences.getString("USER", null)
        if (data != null) {
            user = gson.fromJson(data, User::class.java)
            txtWelcome.setText("Welcome, ${user.gua_fisrtname}")
        }
        //end Welcome Message

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (navDrawertoggleButton.onOptionsItemSelected(item)) {
            return true
        }
        return false
    }

    private fun logout() {

    }

    override fun onResume() {
        super.onResume()

        if (!this.nfcActivationOnResume()) btnRFID.visibility = View.GONE

    }

    override fun onPause() {
        this.nfcCloseOnPause()
        super.onPause()
    }

    override fun onNewIntent(intent: Intent?) {
        if (NfcAdapter.getDefaultAdapter(this) != null) {
            intent?.let { it ->
                val tag = it.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
                if (tag != null) {
                    visitorViewModel.getVisitorByTag(tag.getTagId()).observe(this) { visitor ->
                        if (visitor != null) {
                            val intent: Intent = Intent(this, MovementRecordActivity::class.java)
                            intent.putExtra("VISITOR_ID", visitor.vis_id)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "No Visitor Found", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
            }
            super.onNewIntent(intent)
        }
    }

    companion object {
        private val TAG: String = HomeActivity::class.java.simpleName
        private const val NEW_VISITOR_ACTIVITY_REQUEST_CODE = 1
        const val RESULT = "RESULT"
    }
}

