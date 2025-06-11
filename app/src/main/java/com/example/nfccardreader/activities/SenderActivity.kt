package com.example.nfccardreader.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.nfccardreader.CardEmulatorService
import com.example.nfccardreader.databinding.ActivitySenderBinding

class SenderActivity : AppCompatActivity() {

    private var _binding: ActivitySenderBinding? = null
    private val binding get() = _binding!!
    private var nfcAdapter: NfcAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySenderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        val pm = packageManager
        val hasHCE = pm.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)
        Log.d(TAG, "HCE Supported: $hasHCE")
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)



        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()

            if (message.isNotEmpty()) {
                val intent = Intent(this@SenderActivity, CardEmulatorService::class.java).apply {
                    putExtra("MESSAGE_KEY", message)
                }
                startService(intent)
                Log.d(TAG, "Sent Message: $message")
            } else {
                Log.e(TAG, "Message is empty!")
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (nfcAdapter != null) {
            nfcAdapter?.disableForegroundDispatch(this)
            Log.d("SenderActivity", "Foreground dispatch disabled.")
        }
    }

    override fun onPause() {
        super.onPause()
        if (nfcAdapter != null) {
            nfcAdapter?.disableForegroundDispatch(this)
            Log.d("SenderActivity", "Foreground dispatch disabled.")
        }
    }
    override fun onBackPressed() {
        super.onBackPressed()

        val nIntent= Intent(this@SenderActivity, CardEmulatorService::class.java)
        stopService(nIntent)

    }
}