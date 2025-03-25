package com.example.nfccardreader

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.nfccardreader.databinding.ActivityMain2Binding

class MainActivity2 : AppCompatActivity() {

    private var _binding: ActivityMain2Binding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        val pm = packageManager
        val hasHCE = pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)
        Log.d("DeviceHCE", "HCE Supported: $hasHCE")


        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()

            if (message.isNotEmpty()) {
                val intent = Intent(this@MainActivity2, CardEmulatorService::class.java).apply {
                    putExtra("MESSAGE_KEY", message)
                }
                startService(intent)
                Log.d("MainActivity2", "Sent Message: $message")
            } else {
                Log.e("MainActivity2", "Message is empty!")
            }
        }

    }

}