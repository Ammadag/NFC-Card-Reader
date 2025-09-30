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

                val responses = mutableListOf<Pair<String, String>>() // Pair of (hex, readable)
                val readableOutput = StringBuilder()

                // Add card UID
                val uid = tag.id.joinToString(":") { "%02X".format(it) }
                readableOutput.append("═══════════════════════════════\n")
                readableOutput.append("        CARD INFORMATION\n")
                readableOutput.append("═══════════════════════════════\n\n")
                readableOutput.append("Card UID: $uid\n\n")

                for ((index, command) in cmd.commands.withIndex()) {
                    try {
                        val response = isoDep.transceive(command)
                        Log.d("APDU Response", "$response")
                        val hexResponse = response.joinToString(" ") { "%02X".format(it) }

                        // Parse the response
                        val readable = parseApduResponse(response, index)
                        responses.add(Pair(hexResponse, readable))

                        if (readable.isNotEmpty()) {
                            readableOutput.append("$readable\n")
                        }
                    } catch (e: Exception) {
                        val errorMsg = "Error with command $index: ${e.message}"
                        responses.add(Pair(errorMsg, errorMsg))
                        readableOutput.append("$errorMsg\n")
                    }
                }

                // Save both hex and readable data
                responses.forEachIndexed { i, (hex, readable) ->
                    saveCardData(i, hex)
                }

                showEmulationPrompt()

                // Display readable output
                textView.text = readableOutput.toString()
                Log.d("Card Data", readableOutput.toString())

                isoDep.close()
            } catch (e: Exception) {
                Toast.makeText(this, "Error reading card: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun parseApduResponse(response: ByteArray, commandIndex: Int): String {
        if (response.size < 2) return ""

        val sw1 = response[response.size - 2].toInt() and 0xFF
        val sw2 = response[response.size - 1].toInt() and 0xFF
        val statusWord = (sw1 shl 8) or sw2
        val data = response.copyOfRange(0, response.size - 2)

        val result = StringBuilder()

        // Check status word
        when (statusWord) {
            0x9000 -> {
                // Success - parse data based on command type
                if (data.isNotEmpty()) {
                    result.append("Command $commandIndex: SUCCESS\n")
                    result.append("─────────────────────────────\n")

                    // Try to parse as TLV (Tag-Length-Value)
                    val parsed = parseTLVData(data)
                    if (parsed.isNotEmpty()) {
                        result.append(parsed)
                    } else {
                        // If not TLV, try to extract readable text
                        val text = extractReadableText(data)
                        if (text.isNotEmpty()) {
                            result.append("Data: $text\n")
                        } else {
                            result.append("Raw Data: ${data.joinToString(" ") { "%02X".format(it) }}\n")
                        }
                    }
                }
            }
            0x6A82 -> result.append("Command $commandIndex: File not found\n")
            0x6A86 -> result.append("Command $commandIndex: Incorrect parameters\n")
            0x6982 -> result.append("Command $commandIndex: Security status not satisfied\n")
            0x6985 -> result.append("Command $commandIndex: Conditions not satisfied\n")
            0x6D00 -> result.append("Command $commandIndex: Instruction not supported\n")
            else -> result.append("Command $commandIndex: Status 0x${statusWord.toString(16).uppercase()}\n")
        }

        return result.toString()
    }

    private fun parseTLVData(data: ByteArray): String {
        val result = StringBuilder()
        var i = 0

        while (i < data.size) {
            if (i >= data.size) break

            val tag = data[i].toInt() and 0xFF
            i++

            if (i >= data.size) break

            var length = data[i].toInt() and 0xFF
            i++

            // Handle multi-byte length
            if (length == 0x81 && i < data.size) {
                length = data[i].toInt() and 0xFF
                i++
            } else if (length == 0x82 && i + 1 < data.size) {
                length = ((data[i].toInt() and 0xFF) shl 8) or (data[i + 1].toInt() and 0xFF)
                i += 2
            }

            if (i + length > data.size) break

            val value = data.copyOfRange(i, i + length)
            i += length

            // Interpret common tags
            when (tag) {
                0x4F -> result.append("Application ID: ${value.joinToString("") { "%02X".format(it) }}\n")
                0x50 -> result.append("Application Label: ${extractReadableText(value)}\n")
                0x5A -> result.append("Card Number: ${formatCardNumber(value)}\n")
                0x5F20 -> result.append("Cardholder Name: ${extractReadableText(value)}\n")
                0x5F24 -> result.append("Expiry Date: ${formatExpiryDate(value)}\n")
                0x5F28 -> result.append("Issuer Country: ${extractReadableText(value)}\n")
                0x5F30 -> result.append("Service Code: ${value.joinToString("") { "%02X".format(it) }}\n")
                0x87 -> result.append("Application Priority: ${value[0]}\n")
                0x9F08 -> result.append("App Version: ${value.joinToString(".") { "${it.toInt() and 0xFF}" }}\n")
                0x9F12 -> result.append("Application Name: ${extractReadableText(value)}\n")
                0x9F42 -> result.append("Currency Code: ${getCurrencyCode(value)}\n")
                else -> {
                    val text = extractReadableText(value)
                    if (text.isNotEmpty() && text.length > 2) {
                        result.append("Tag 0x${tag.toString(16).uppercase()}: $text\n")
                    }
                }
            }
        }

        return result.toString()
    }

    private fun extractReadableText(data: ByteArray): String {
        return data.filter { it in 32..126 }.map { it.toInt().toChar() }.joinToString("")
    }

    private fun formatCardNumber(data: ByteArray): String {
        val cardNumber = data.joinToString("") { String.format("%02X", it) }
        return cardNumber.chunked(4).joinToString(" ")
    }

    private fun formatExpiryDate(data: ByteArray): String {
        if (data.size >= 2) {
            val year = data[0].toInt() and 0xFF
            val month = data[1].toInt() and 0xFF
            return "${String.format("%02d", month)}/${String.format("%02d", year)}"
        }
        return data.joinToString("") { "%02X".format(it) }
    }

    private fun getCurrencyCode(data: ByteArray): String {
        if (data.size >= 2) {
            val code = ((data[0].toInt() and 0xFF) shl 8) or (data[1].toInt() and 0xFF)
            return when (code) {
                840 -> "USD (US Dollar)"
                978 -> "EUR (Euro)"
                826 -> "GBP (British Pound)"
                586 -> "PKR (Pakistani Rupee)"
                356 -> "INR (Indian Rupee)"
                else -> code.toString()
            }
        }
        return "Unknown"
    }

    private fun saveCardData(index: Int, data: String) {
        val prefs = getSharedPreferences("CardData", MODE_PRIVATE)
        prefs.edit().putString(index.toString(), data).apply()
    }

    private fun showEmulationPrompt() {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Card Saved")
            .setMessage("Do you want to emulate this card?")
            .setPositiveButton("Yes") { _, _ ->
                startCardEmulation()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun startCardEmulation() {
        val prefs = getSharedPreferences("CardData", MODE_PRIVATE)
        val apduResponses = (0 until prefs.all.size).mapNotNull { prefs.getString(it.toString(), null) }

        if (apduResponses.isNotEmpty()) {
            Toast.makeText(this, "Card emulation ready", Toast.LENGTH_SHORT).show()
            // Let the system know that emulation can proceed (if required)
        } else {
            Toast.makeText(this, "No card data available to emulate", Toast.LENGTH_LONG).show()
        }
    }
}