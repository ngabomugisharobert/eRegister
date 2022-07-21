package com.hogl.eregister.activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.view.MenuItem
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
import com.hogl.eregister.utils.GenerateVisitorId
import com.hogl.eregister.utils.SessionManagement


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


    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()

        var gson: Gson = Gson()
        var data: String? = sharedPreferences.getString("USER", null)
        if (data != null) {
            user = gson.fromJson(data, User::class.java)
            txtWelcome.setText("Welcome, ${user.gua_fisrtname}")
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
                    val visitor = Visitor(
                        GenerateVisitorId.getId(),
                        vis_first_name,
                        vis_last_name,
                        vis_phone.toInt(),
                        vis_type,
                        vis_idNumber,
                        "",
                        "",
                        System.currentTimeMillis()
                    )
//                    TODO NFC ID CArd and QR CODE TO BE Implemented
                    visitorViewModel.insert(visitor)

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

    }

    private fun initComponents() {
        this.title = ""

        btnRegistered = binding.crdRegistered
        btnNewVisitor = binding.crdNewVisitor
        btnSync = binding.crdSync
        drawerLayout = binding.drawerLayout
        nav_layout = binding.navView
        container = binding.container

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
                R.id.item1 -> {Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show()
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
                    val inflater = inflate.inflate(R.layout.loading_toast,null)

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

        txtWelcome = binding.txtWelcome
        sharedPreferences =
            applicationContext.getSharedPreferences("preferences", Context.MODE_PRIVATE)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (navDrawertoggleButton.onOptionsItemSelected(item)) {
            return true
        }
        return false
    }


    private fun Context.isDarkThemeOn(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }

    private fun logout(){

    }


    companion object {
        private val TAG: String = HomeActivity::class.java.simpleName
        private const val NEW_VISITOR_ACTIVITY_REQUEST_CODE = 1
    }
}

