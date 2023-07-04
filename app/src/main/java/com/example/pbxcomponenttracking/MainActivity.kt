package com.example.pbxcomponenttracking

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView

import android.text.TextWatcher
import android.util.Log
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.w3c.dom.Text
import java.nio.charset.Charset


class MainActivity : AppCompatActivity() {
    private lateinit var frameSNText: TextView
    private lateinit var CmpSNText: TextView
    private lateinit var CmpTypeText: TextView
    private lateinit var CmpPNText: TextView
    private lateinit var barcodeString : String



    private val intentLauncherFrame =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // update the TextView fields
                findViewById<TextView>(R.id.FramePNText).text = result.data?.getStringExtra("part_number").toString()
                findViewById<TextView>(R.id.FrameSNText).text = result.data?.getStringExtra("serial_number").toString()
                findViewById<TextView>(R.id.FrameDDText).text = result.data?.getStringExtra("DD_number").toString()

                // set the background of the frame tile to green
//                findViewById<CardView>(R.id.CardFrame).backgroundTintList = R.color.green

            }

        }

    private val intentLauncherCmp =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // update the TextView fields
                findViewById<TextView>(R.id.CmpTypeText).text = result.data?.getStringExtra("type").toString()
                findViewById<TextView>(R.id.CmpPNText).text = result.data?.getStringExtra("part_number").toString()
                findViewById<TextView>(R.id.CmpSNText).text = result.data?.getStringExtra("serial_number").toString()
                findViewById<TextView>(R.id.CmpDDText).text = result.data?.getStringExtra("DD_number").toString()


            // set the background of the frame tile to green
//                findViewById<CardView>(R.id.CardFrame).backgroundTintList = R.color.green
            }

        }

    private val listenerScanFrame = View.OnClickListener { ImageView ->
        when (ImageView.id){
            R.id.scanFrameBtn -> {
//                val intent = Intent(this, QrScanning::class.java)
//                startActivity(intent)
                val myIntent = Intent(this, QrScanning::class.java)
                var foo_bundle = Bundle()
                foo_bundle.putString("targetType", "frame")
                intentLauncherFrame.launch(myIntent.putExtras(foo_bundle))
            }
        }
    }

    private val listenerScanCmp = View.OnClickListener { ImageView ->
        when (ImageView.id){
            R.id.scanCmpBtn -> {
//                val intent = Intent(this, QrScanning::class.java)
//                startActivity(intent)
                val myIntent = Intent(this, QrScanning::class.java)
                var foo_bundle = Bundle()
                foo_bundle.putString("targetType", "cmp")
                intentLauncherCmp.launch(myIntent.putExtras(foo_bundle))
            }
        }
    }

    private val listenerSendLink = View.OnClickListener { ImageView ->
        when (ImageView.id){
            R.id.link_Button -> {
                postComponentLink()
                clearTextFields()
            }
        }
    }

    private fun postComponentLink() {
        val queue = Volley.newRequestQueue(this)
        val url =
            "https://script.google.com/macros/s/AKfycbzX_wCGTw9JU0KbZJEHltu2Dktdma1JtW4ARAFDairCkHFP6mc5LiVnT4y92RhqFgYTsA/exec"

//        val requArray = listOf<String>(
//            findViewById<TextView>(R.id.FramePNText).text.toString(),
//            findViewById<TextView>(R.id.FrameSNText).text.toString(),
//            findViewById<TextView>(R.id.CmpPNText).text.toString(),
//            findViewById<TextView>(R.id.CmpSNText).text.toString(),
//
//        )
//        val requestBody = requArray.joinToString(separator = "&")
        val requArray = listOf<String>(
            "action=addLine",
            "vName="+findViewById<TextView>(R.id.FrameSNText).text.toString(),
            "vNumber="+findViewById<TextView>(R.id.CmpSNText).text.toString(),
        )
        val requestBody = requArray.joinToString(separator = "&")
        val stringReq: StringRequest =
            object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    // response
                    val strResp = response.toString()
                    Log.d("API", strResp)
                },
                Response.ErrorListener { error ->
                    Log.d("API", "error => $error")
                }
            ) {
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }

        queue.add(stringReq)
    }

    private fun clearTextFields(){
        findViewById<TextView>(R.id.FramePNText).text = ""
        findViewById<TextView>(R.id.FrameDDText).text = ""
        findViewById<TextView>(R.id.FrameSNText).text = ""
        findViewById<TextView>(R.id.CmpTypeText).text = ""
        findViewById<TextView>(R.id.CmpPNText).text = ""
        findViewById<TextView>(R.id.CmpDDText).text = ""
        findViewById<TextView>(R.id.CmpSNText).text = ""
    }


    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(p0: Editable?) {
            findViewById<Button>(R.id.link_Button).isEnabled = findViewById<TextView>(R.id.FrameSNText).text != "" && findViewById<TextView>(R.id.CmpSNText).text != ""
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.scanFrameBtn).setOnClickListener(listenerScanFrame)
        findViewById<Button>(R.id.scanCmpBtn).setOnClickListener(listenerScanCmp)
        findViewById<Button>(R.id.link_Button).setOnClickListener(listenerSendLink)

        // add textWatcher
        findViewById<TextView>(R.id.CmpSNText).addTextChangedListener(textWatcher)
        findViewById<TextView>(R.id.FrameSNText).addTextChangedListener(textWatcher)
    }
}