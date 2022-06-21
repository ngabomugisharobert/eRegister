package com.example.eregister

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.eregister.activities.HomeActivity
import com.example.eregister.lifecycle.MainActivityObserver
import com.example.eregister.utils.DBFileProvider


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        backupDatabase(this)

        Log.i(TAG,"Activity onCreated")
        lifecycle.addObserver(MainActivityObserver())

    }
    fun fnLogin(view: View){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    companion object{
        private val TAG:String = MainActivityObserver::class.java.simpleName
        private const val NEW_VISITOR_ACTIVITY_REQUEST_CODE = 1
    }

    fun backupDatabase(activity: AppCompatActivity?) {
        val uri = DBFileProvider().getDatabaseURI(activity!!, "eRegister_database")
        if (uri != null) {
            sendEmail(activity, uri)
        }
    }

    private fun sendEmail(activity: AppCompatActivity, attachment: Uri) {
        val emailIntent = Intent(Intent.ACTION_SEND)
        //Set type to email
        emailIntent.type = "vnd.android.cursor.dir/email"
        val toEmail = "whatever@gmail.com"
        emailIntent.putExtra(Intent.EXTRA_EMAIL, toEmail)
        emailIntent.putExtra(Intent.EXTRA_STREAM, attachment)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Data for Training Log")
        activity.startActivity(Intent.createChooser(emailIntent, "Send Email"))

    }

}