package com.example.nfccardreader

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.*
import android.nfc.tech.Ndef
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.nio.charset.Charset

class NFCTransferActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var textView: TextView
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private var isSendingMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device", Toast.LENGTH_LONG).show()
            finish()
        }

        sendButton.setOnClickListener {
            isSendingMode = true
            Toast.makeText(this, "Tap an NFC-enabled device to send the message!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED))
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, filters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        if (tag != null) {
            if (isSendingMode) {
                val message = messageInput.text.toString()
                if (message.isNotEmpty()) {
                    writeNFCMessage(message, tag)
                } else {
                    Toast.makeText(this, "Enter a message first!", Toast.LENGTH_SHORT).show()
                }
            } else {
                readNFCMessage(intent)
            }
        }
    }

    private fun writeNFCMessage(message: String, tag: Tag) {
        try {
            val ndefMessage = createNdefMessage(message)
            val ndef = Ndef.get(tag)

            if (ndef != null) {
                ndef.connect()
                if (ndef.isWritable) {
                    ndef.writeNdefMessage(ndefMessage)
                    Toast.makeText(this, "Message Sent!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Tag is not writable!", Toast.LENGTH_SHORT).show()
                }
                ndef.close()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readNFCMessage(intent: Intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.action || NfcAdapter.ACTION_TECH_DISCOVERED == intent.action && !isSendingMode) {
            val rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
            val messages = rawMessages?.map { it as NdefMessage }
            messages?.let {
                for (message in it) {
                    for (record in message.records) {
                        val text = String(record.payload, Charsets.UTF_8)
                        textView.text = "Received: $text"
                        Toast.makeText(this, "Message Received: $text", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun createNdefMessage(text: String): NdefMessage {
        val textBytes = text.toByteArray(Charset.forName("UTF-8"))
        val record = NdefRecord.createMime("text/plain", textBytes)
        return NdefMessage(arrayOf(record))
    }
}
