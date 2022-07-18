package com.hogl.eregister

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hogl.eregister.activities.HomeActivity
import com.hogl.eregister.data.InitApplication
import com.hogl.eregister.data.models.GuardViewModel
import com.hogl.eregister.data.models.GuardViewModelFactory
import com.hogl.eregister.lifecycle.MainActivityObserver
import com.hogl.eregister.utils.SessionManagement
import com.google.gson.Gson
import com.hogl.eregister.databinding.ActivityLoginBinding
import com.hogl.eregister.databinding.ActivityRegisteredVisitorBinding
import java.util.*
import kotlin.concurrent.schedule


class LoginActivity : AppCompatActivity() {
    private lateinit var txtUsername: TextView
    private lateinit var txtPassword: TextView
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var btnForgotPassword: Button
    private lateinit var loginProgressBar: ProgressBar

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var selected:Int = -1

    private val gates = arrayOf("Gate 1", "Gate 2", "Gate 3", "Gate 4")

    private val guardViewModel: GuardViewModel by viewModels {
        GuardViewModelFactory((application as InitApplication).guardRepository)
    }

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
    }

    override fun onStart() {
        super.onStart()
        checkSession()
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

            btnLogin.setOnClickListener {
                guardViewModel.checkLogin(
                    txtUsername.text.toString(),
                    txtPassword.text.toString()
                ).observe(this) {
                    if (it != null) {
                        Toast.makeText(
                            this@LoginActivity,
                            R.string.loginSuccess,
                            Toast.LENGTH_SHORT
                        ).show()

                        val mAlertDialogBuilder = AlertDialog.Builder(this@LoginActivity)
                        mAlertDialogBuilder.setTitle(R.string.selectGate)
                        mAlertDialogBuilder.setCancelable(false)
                        mAlertDialogBuilder.setSingleChoiceItems(
                            gates,
                            -1
                        ) { dialog, which ->
                            when (which) {
                                which -> {
                                    selected = which+1
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
                                User(it.gua_id, it.gua_first_name, it.gua_last_name, it.gua_username, selected)
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

    private fun initComponents(){
        sharedPreferences = applicationContext.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        editor=  sharedPreferences.edit()

        txtUsername = binding.txtUsername
        txtPassword = binding.txtPassword
        btnLogin = binding.btnLogin
        loginProgressBar = binding.loginProgressBar
        lifecycle.addObserver(MainActivityObserver())
    }
    companion object {
        private val TAG: String = LoginActivity::class.java.simpleName
    }
}