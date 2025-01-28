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

                for (command in cmd.commands) {
                    try {
                        val response = isoDep.transceive(command)
                        val parsedResponse = response.joinToString(" ") { "%02X".format(it) }
                        responses.add(parsedResponse)
                    } catch (e: Exception) {
                        responses.add("Error with command: ${e.message}")
                    }
                }

                responses.forEachIndexed { i, m ->
                    saveCardData(i,m)
                }
                textView.text = responses.joinToString("\n")

                Log.d("APDU Responses", responses.joinToString("\n"))
                isoDep.close()
            } catch (e: Exception) {
                Toast.makeText(this, "Error reading card: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun saveCardData(index : Int, data: String) {
        val prefs = getSharedPreferences("CardData", MODE_PRIVATE)
        prefs.edit().putString(index.toString(), data).apply()
    }


}


