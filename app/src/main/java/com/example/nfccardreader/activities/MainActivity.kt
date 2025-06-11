package com.example.nfccardreader.activities

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.nfccardreader.CardEmulatorService
import com.example.nfccardreader.R


class MainActivity : ComponentActivity() {


    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        val buttonSend = findViewById<Button>(R.id.btn_Sender)
        val buttonReceive = findViewById<Button>(R.id.btn_reciever)

        buttonSend.setOnClickListener {
            nfcAdapter = null
            val intent = Intent(this, SenderActivity::class.java)
            startActivity(intent)
        }

        buttonReceive.setOnClickListener {
            stopHceService()
            val intent = Intent(this, CardReaderActivity::class.java)
            startActivity(intent)
        }

    }

    private fun stopHceService() {
        val intent = Intent(this, CardEmulatorService::class.java)
        stopService(intent)
    }

}
