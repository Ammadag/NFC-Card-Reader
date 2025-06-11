package com.example.nfccardreader.activities

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.nfccardreader.CardEmulatorService
import com.example.nfccardreader.R


const val TAG = "NFC"

class CardReaderActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var textView: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_reader)

        textView = findViewById(R.id.textView)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)


        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not supported on this device", Toast.LENGTH_LONG).show()
            finish()
        }

        val intent = Intent(this, CardEmulatorService::class.java)
        stopService(intent)
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        super.onBackPressed()
        Log.d(TAG, "back pressed from card reader activity")
        nfcAdapter = null
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume Called in Card Reader Activity")
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, null, null)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause Called in Card Reader Activity")
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
                    0xF0.toByte(), 0x00, 0x00, 0x00, 0x01, 0x01, 0x01
                )

                val aidResponse = isoDep.transceive(selectAIDCommand)
                val parsedAidResponse = aidResponse.joinToString(" ") { "%02X".format(it) }
                Toast.makeText(this, "Aid Selection Successfull", Toast.LENGTH_LONG).show()

                Log.d(TAG, "AID Response: $parsedAidResponse")

                // Check if AID was successfully selected
                if (parsedAidResponse == "90 00") {
                    // Send Custom Command
                    val customCommand = byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00, 0x10)
                    val response = isoDep.transceive(customCommand)
                    val parsedResponse = response.toString(Charsets.UTF_8).trim()

                    textView.text = "Response: $parsedResponse"
                    Log.d(TAG, "Custom Command Response: $parsedResponse")
                } else {
                    Log.d(TAG, "AID Selection Failed")
                }

                isoDep.close()
            } catch (e: Exception) {
                Toast.makeText(this, "Error reading card: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "on Destroy called in Card reader Activity")
        nfcAdapter = null
    }
}


