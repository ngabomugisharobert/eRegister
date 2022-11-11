package com.hogl.eregister.utils

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.nfc.NfcAdapter
import android.nfc.tech.NfcA
import android.view.View
import android.widget.Toast
import androidx.cardview.widget.CardView
import java.io.File
import java.io.FileOutputStream

fun Context.getFolder(): File
{
    val directory = File("${this.filesDir}/json")
    if(!directory.exists())
    {
        directory.mkdir()
    }
    return directory
}

fun Context.buildJSON(data: ByteArray)
{
    val directory = this.getFolder()
    val file = File(directory,"data.json")

    var fos: FileOutputStream? = null
    fos = FileOutputStream(file)
    fos.write(data)
    fos.close()
}

fun Context.nfcActivation():Boolean
{
    var nfcAdapter: NfcAdapter
    //NFC READER
    if (NfcAdapter.getDefaultAdapter(this) != null) {

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        // NFC not available on this device
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is disabled", Toast.LENGTH_LONG).show()
            return false
        } else {
            if (!nfcAdapter.isEnabled) {
                Toast.makeText(this, "NFC is disabled", Toast.LENGTH_LONG).show()
                return false
            }
        }
        return true
    }
    else {
        return false
    }
}

fun Context.nfcActivationOnResume():Boolean
{
    val NFC_TYPES = arrayOf(
        arrayOf(NfcA::class.java.name),
    )
    var nfcAdapter: NfcAdapter
    if (NfcAdapter.getDefaultAdapter(this) != null) {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

            if (!nfcAdapter.isEnabled) {
                Toast.makeText(this, "NFC is disabled", Toast.LENGTH_LONG).show()
                return false
            }

        // Correspondence to prevent other apps from opening
        val intent = Intent(applicationContext, this::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val nfcPendingIntent =
            PendingIntent.getActivity(
                applicationContext,
                0,
                intent,
                0 or PendingIntent.FLAG_MUTABLE
            )

        // NFC IntentFilter for reading
        val techDetectedFilter = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        val ndefDetectedFilter =
            IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply { addDataType("*/*") } // この指定な無くても大丈夫
        val urlDetectedFilter = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED) // この指定は無くても大丈夫
        urlDetectedFilter.addDataScheme("http")
        urlDetectedFilter.addDataScheme("https")

        // Allow loading only while the app is running
        nfcAdapter.enableForegroundDispatch(
            this as Activity,
            nfcPendingIntent,
            arrayOf(techDetectedFilter, ndefDetectedFilter, urlDetectedFilter),
            NFC_TYPES
        )
        return true
    }
    else {
        return false
    }
}

fun Context.nfcCloseOnPause()
{
    var nfcAdapter:NfcAdapter
    if (NfcAdapter.getDefaultAdapter(this) != null) {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter != null) {
            if ((this as Activity).isFinishing) {
                nfcAdapter.disableForegroundDispatch(this)
            }
        }
    }
}

fun Context.isDarkThemeOn(): Boolean {
    return resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
}