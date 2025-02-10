package com.example.nfccardreader

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity


class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var textView: TextView
    private val cmd = cmds()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)

        // Initialize NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not supported on this device", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        if (tag != null) {
            readFromNfcTag(tag)
        }
    }


    private fun readFromNfcTag(tag: Tag) {
        val techList = tag.techList.joinToString(", ")
        Toast.makeText(this, "Tech List: $techList", Toast.LENGTH_LONG).show()

        if (techList.contains("android.nfc.tech.IsoDep")) {
            val isoDep = android.nfc.tech.IsoDep.get(tag)
            try {
                isoDep.connect()

                // Select AID
                val selectAIDCommand = byteArrayOf(
                    0x00, 0xA4.toByte(), 0x04, 0x00, 0x07,
                    0xA0.toByte(), 0x00, 0x00, 0x00, 0x04, 0x10, 0x10
                )

                val aidResponse = isoDep.transceive(selectAIDCommand)
                val parsedAidResponse = aidResponse.joinToString(" ") { "%02X".format(it) }
                Log.d("APDU Response", "AID Response: $parsedAidResponse")

                // Check if AID was successfully selected
                if (parsedAidResponse == "90 00") {
                    // Send Custom Command
                    val customCommand = byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00, 0x10)
                    val response = isoDep.transceive(customCommand)
                    val parsedResponse = response.toString(Charsets.UTF_8).trim()

                    textView.text = "Response: $parsedResponse"
                    Log.d("APDU Response", "Custom Command Response: $parsedResponse")
                } else {
                    Log.d("APDU Response", "AID Selection Failed")
                }

                isoDep.close()
            } catch (e: Exception) {
                Toast.makeText(this, "Error reading card: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}


