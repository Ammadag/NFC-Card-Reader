package com.example.nfccardreader.activities

import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import com.example.nfccardreader.CardEmulatorService
import com.example.nfccardreader.R
import com.example.nfccardreader.utils.usersession


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSend = findViewById<Button>(R.id.btn_Sender)
        val buttonReceive = findViewById<Button>(R.id.btn_reciever)
        var sending = usersession.isSending
        var receiving = usersession.isRecieving

        buttonSend.setOnClickListener {
            sending = true
            receiving = false
            disableNfcReaderMode()
            val intent = Intent(this, SenderActivity::class.java)
            startActivity(intent)

        }
        buttonReceive.setOnClickListener {
            sending = false
            receiving = true
            stopHceService()
            val intent = Intent(this, CardReaderActivity::class.java)
            startActivity(intent)

        }

    }
    fun stopHceService() {
        val intent = Intent(this, CardEmulatorService::class.java)
        stopService(intent)
    }

    fun disableNfcReaderMode() {
        val nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        nfcAdapter.disableReaderMode(this)
    }
}
