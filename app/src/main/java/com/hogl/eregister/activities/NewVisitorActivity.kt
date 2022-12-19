package com.hogl.eregister.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.text.InputType
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.textfield.TextInputEditText
import com.hogl.eregister.R
import com.hogl.eregister.data.InitApplication
import com.hogl.eregister.data.entities.visitor.Visitor
import com.hogl.eregister.databinding.ActivityNewVisitorBinding
import com.hogl.eregister.extensions.TagExtension.getTagId
import com.hogl.eregister.utils.nfcActivation
import com.hogl.eregister.utils.nfcActivationOnResume
import com.hogl.eregister.utils.nfcCloseOnPause
import com.squareup.okhttp.internal.DiskLruCache
import com.hogl.eregister.data.models.VisitorViewModel
import com.hogl.eregister.data.models.VisitorViewModelFactory

class NewVisitorActivity : AppCompatActivity() {
    private lateinit var visitorType: Spinner
    private lateinit var txt_visitor_fn: TextInputEditText
    private lateinit var txt_visitor_ln: TextView
    private lateinit var txt_visitor_phone: TextView
    private lateinit var btn_save_visitor: Button
    private lateinit var txt_vis_idNumber: TextView
    private lateinit var nfc_complete : LottieAnimationView
    private lateinit var nfc_error : LottieAnimationView
    private lateinit var nfc_loading : LottieAnimationView

    private lateinit var options: Array<String>
    private lateinit var visitor_type: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivityNewVisitorBinding

    private val visitorViewModel: VisitorViewModel by viewModels {
        VisitorViewModelFactory((this.application as InitApplication).visitorRepository)
    }

    private var isModification = false
    private var tagId :String = "none"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityNewVisitorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()

        //get visitor id from intent after checking if it is null
        val visitorId = intent.getLongExtra("VISITOR_ID", 0)
        if (visitorId != 0L) {

            this.title = getString(R.string.modification)
            btn_save_visitor.text = getString(R.string.update)

            isModification = true
            visitorViewModel.getVisitorById(visitorId.toString()).observe(this) {
                it?.let {
                    txt_visitor_fn.setText(it.vis_first_name)
                    txt_visitor_ln.setText(it.vis_last_name)
                    txt_visitor_phone.setText(it.vis_phone.toString())
                    txt_vis_idNumber.setText(it.vis_IDNumber)
                    visitorType.setSelection(options.indexOf(it.vis_type))
                }
            }
        }

        visitorType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                visitor_type = options.get(p2)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                visitor_type = options[6]
            }

        }

        if(!this.nfcActivation()) {
            nfc_loading.visibility = View.GONE
        }
        else {
            nfc_loading.visibility = View.VISIBLE
        }


        btn_save_visitor.setOnClickListener {
            var resultIntent = Intent()
            if (
                TextUtils.isEmpty(txt_visitor_fn.text) ||
                TextUtils.isEmpty(txt_visitor_ln.text) ||
                TextUtils.isEmpty(txt_visitor_phone.text) ||
                TextUtils.isEmpty(visitor_type) ||
                TextUtils.isEmpty(txt_vis_idNumber.text)
            ) {
                Toast.makeText(this, R.string.allField, Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_CANCELED, resultIntent)
            } else {
                if(!isModification) {

                        this.title = getString(R.string.modification)
                        btn_save_visitor.text = getString(R.string.update)

                    resultIntent.putExtra(VIS_FIRST_NAME, txt_visitor_fn.text.toString())
                    resultIntent.putExtra(VIS_LAST_NAME, txt_visitor_ln.text.toString())
                    resultIntent.putExtra(VIS_PHONE, txt_visitor_phone.text.toString())
                    resultIntent.putExtra(VIS_TYPE, visitor_type)
                    resultIntent.putExtra(VIS_ID_NUMBER, txt_vis_idNumber.text.toString())
                    resultIntent.putExtra(VIS_NFC_CARD, tagId)
                    setResult(Activity.RESULT_OK, resultIntent)
                }
                else{


                    this.title = getString(R.string.new_visitor)
                    btn_save_visitor.text = getString(R.string.save)

                    Toast.makeText(this, "updated : ", Toast.LENGTH_SHORT).show()
//                    updata a visitor in the database
                    visitorViewModel.updateVisitor(
                        Visitor(
                            visitorId,
                            txt_visitor_fn.text.toString(),
                            txt_visitor_ln.text.toString(),
                            txt_visitor_phone.text.toString().toInt(),
                            visitor_type,
                            txt_vis_idNumber.text.toString(),
                            tagId,
                            "qr",
                            0
                        )
                    )
                    //set result to ok
                    setResult(Activity.RESULT_OK, resultIntent)

                }
            }
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        if(!this.nfcActivationOnResume()) {
            nfc_loading.visibility = View.GONE
        }
        else {
            nfc_loading.visibility = View.VISIBLE
        }

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
                    nfc_complete.visibility= View.VISIBLE
                    nfc_loading.visibility= View.GONE
                    tagId = (tag.getTagId()).toString()
                    Log.d("NFC", tagId)
//                    check if the tagId is already in the database
                    visitorViewModel.getVisitorByNfc(tagId).observe(this) {itt ->
                        if (itt != null) {
                            nfc_error.visibility = View.VISIBLE
                            nfc_complete.visibility = View.GONE
                            Toast.makeText(this, "NFC card already in use", Toast.LENGTH_SHORT)
                                .show()
                        }
                        else {
                            nfc_error.visibility = View.GONE
                            nfc_complete.visibility = View.VISIBLE
                        }
                    }
                }
                else{
                    nfc_error.visibility= View.VISIBLE
                    nfc_loading.visibility= View.GONE
                }
            }
            super.onNewIntent(intent)
        }
    }

    private fun initComponents() {
        visitorType = binding.spVisitorType
        txt_visitor_fn = binding.txtVisitorFn
        txt_visitor_ln = binding.txtVisitorLn
        txt_visitor_phone = binding.txtVisitorPhone
        btn_save_visitor = binding.btnSaveVisitor
        txt_vis_idNumber = binding.txtVisIdnumber
        nfc_complete = binding.nfcScanCompleted
        nfc_error = binding.nfcScanError
        nfc_loading = binding.nfcScanLoading

        //only accept numbers
    txt_visitor_phone.setInputType(InputType.TYPE_CLASS_NUMBER)

        visitor_type = ""
        options =
            arrayOf("HOGL employee", "RHL", "HOGL casual", "Authorities", "REG", "Guard", "Other")
        sharedPreferences =
            applicationContext.getSharedPreferences("preferences", Context.MODE_PRIVATE)
        visitorType.adapter =
            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)
    }

    companion object {
        private val TAG: String = NewVisitorActivity::class.java.simpleName
        const val VIS_FIRST_NAME = "vis_first_name"
        const val VIS_LAST_NAME = "vis_last_name"
        const val VIS_PHONE = "vis_phone"
        const val VIS_TYPE = "vis_type"
        const val VIS_ID_NUMBER = "VIS_ID_NUMBER"
        const val VIS_NFC_CARD = "VIS_NFC_CARD"
    }
}