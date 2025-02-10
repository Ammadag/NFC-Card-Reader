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
    private val cmd  = cmds()

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

                val responses = mutableListOf<String>()

                // Define custom command
                val customCommand = byteArrayOf(
                    0x00, 0xA4.toByte(), 0x04, 0x00, 0x07, // Example command structure
                    0xA0.toByte(), 0x00, 0x00, 0x00, 0x04, 0x10, 0x10 // Example data
                )

                try {
                    // Send custom APDU command
                    val response = isoDep.transceive(customCommand)

                    // Convert byte array to readable string
                    val parsedResponse = response.joinToString(" ") { "%02X".format(it) }

                    // Add response to the list
                    responses.add("Custom Command Response: $parsedResponse")

                    // Log response
                    Log.d("APDU Custom Response", "Command: ${customCommand.joinToString(" ") { "%02X".format(it) }}\nResponse: $parsedResponse")

                } catch (e: Exception) {
                    val errorMsg = "Error with custom command: ${e.message}"
                    responses.add(errorMsg)
                    Log.e("APDU Error", errorMsg, e)
                }

                // Update UI with response
                runOnUiThread { textView.text = responses.joinToString("\n") }

                isoDep.close()

            } catch (e: Exception) {
                Log.e("NFC_ERROR", "Error reading NFC tag", e)
                Toast.makeText(this, "Error reading card: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "NFC tag does not support IsoDep", Toast.LENGTH_LONG).show()
        }
    }

}


