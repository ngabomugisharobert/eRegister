package com.example.eregister

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.eregister.activities.HomeActivity
import com.example.eregister.data.InitApplication
import com.example.eregister.data.models.GuardViewModel
import com.example.eregister.data.models.GuardViewModelFactory
import com.example.eregister.lifecycle.MainActivityObserver


class LoginActivity : AppCompatActivity() {
    private lateinit var txtUsername : TextView
    private lateinit var txtPassword : TextView
    private lateinit var btnLogin : Button

    private val guardViewModel: GuardViewModel by viewModels {
        GuardViewModelFactory((application as InitApplication).guardRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


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
            moveToHome()
        } else {
            //do nothing

            btnLogin.setOnClickListener {

                        val isLogin = guardViewModel.checkLogin("alex","alex")
                Log.i(TAG,isLogin.isCompleted.toString() +"888888888888888888888888888888")
            }

        }
    }

    fun fnLogin(){

        //1. login and save session

//        val isLogin = guardViewModel.insert(Guard(90,"king","king","king","king","king",43,32,121212))
//        val isLogin = guardViewModel.checkLogin("king","king")
        val user = User(12, "Ankit")
//        Log.i(TAG, "fnLogin: $isLogin ------------------")
        val sessionManagement:SessionManagement = SessionManagement(this@LoginActivity)
        sessionManagement.saveSession(user)

        //2. step

        moveToHome()
    }

    private fun moveToHome() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    companion object{
        private val TAG:String = LoginActivity::class.java.simpleName
    }


}