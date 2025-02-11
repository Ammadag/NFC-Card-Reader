package com.example.nfccardreader.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.nfccardreader.CardEmulatorService
import com.example.nfccardreader.R

class SenderActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sender)
        val pm = packageManager
        val hasHCE = pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)
        Log.d("DeviceHCE", "HCE Supported: $hasHCE")
        val btnSend = findViewById<Button>(R.id.btn_send_msg)
        val message = findViewById<EditText>(R.id.et_send_msg)
        val msg = message.text.toString()

        val bundle = Bundle().apply {
            putString("CUSTOM_MESSAGE", msg)
        }

        btnSend.setOnClickListener {
            val intent = Intent(this, CardEmulatorService::class.java).apply {
                putExtras(bundle)
            }
            startService(intent)
            Handler(Looper.getMainLooper()).postDelayed({
                Log.d("HCE Service", "response sent")
                stopService(intent)
            }, 5000)
        }
    }
}