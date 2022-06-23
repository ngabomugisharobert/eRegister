package com.example.eregister

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.eregister.activities.HomeActivity
import com.example.eregister.data.InitApplication
import com.example.eregister.data.models.GuardViewModel
import com.example.eregister.data.models.GuardViewModelFactory
import com.example.eregister.lifecycle.MainActivityObserver
import com.google.gson.Gson


class LoginActivity : AppCompatActivity() {
    private lateinit var txtUsername: TextView
    private lateinit var txtPassword: TextView
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var btnForgotPassword: Button

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var selected:Int = -1

    private val gates = arrayOf("Gate 1", "Gate 2", "Gate 3", "Gate 4")



    private val guardViewModel: GuardViewModel by viewModels {
        GuardViewModelFactory((application as InitApplication).guardRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        sharedPreferences = applicationContext.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        editor=  sharedPreferences.edit()


        txtUsername = findViewById(R.id.txt_username)
        txtPassword = findViewById(R.id.txt_Password)
        btnLogin = findViewById(R.id.btn_login)
        lifecycle.addObserver(MainActivityObserver())


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
                            "Logged in successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        val mAlertDialogBuilder = AlertDialog.Builder(this@LoginActivity)
                        mAlertDialogBuilder.setTitle("Select Gate")
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

                        mAlertDialogBuilder.setPositiveButton("select") { _, _ ->

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
                        Toast.makeText(
                            this@LoginActivity,
                            "Invalid username or password",
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

    companion object {
        private val TAG: String = LoginActivity::class.java.simpleName
    }


}