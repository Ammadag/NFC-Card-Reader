package com.example.nfccardreader

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity

// Define a type alias for APDU commands
typealias ApduCommand = ByteArray

class MainActivity : ComponentActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private lateinit var textView: TextView

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
        val ndef = Ndef.get(tag)
        val techList = tag.techList.joinToString(", ")
        Toast.makeText(this, "Tech List: $techList", Toast.LENGTH_LONG).show()

        if (techList.contains("android.nfc.tech.Ndef")) {
            Toast.makeText(
                this,
                "This card supports NDEF. Supported techs: $techList",
                Toast.LENGTH_LONG
            ).show()

        } else {
            Toast.makeText(
                this,
                "This card does not support NDEF. Supported techs: $techList",
                Toast.LENGTH_LONG
            ).show()
        }

        if (techList.contains("android.nfc.tech.IsoDep")) {
            val isoDep = android.nfc.tech.IsoDep.get(tag)
            try {
                isoDep.connect()

                // List of APDU commands
                val selectFileCommand: ApduCommand = byteArrayOf(
                    0x00, 0xA4.toByte(), 0x04, 0x00, 0x0E, // AID length
                    0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31 // AID
                )
                val getCardUIDCommand: ApduCommand = byteArrayOf(
                    0xFF.toByte(), 0xCA.toByte(), 0x00, 0x00, 0x00
                )
                val getCardATRCommand: ApduCommand = byteArrayOf(
                    0x00, 0xC0.toByte(), 0x00, 0x00, 0x00
                )
                val readBinaryCommand: ApduCommand = byteArrayOf(
                    0x00, 0xB0.toByte(), 0x00, 0x00, 0x10 // Read 16 bytes
                )
                val getDataCommand: ApduCommand = byteArrayOf(
                    0x80.toByte(), 0xCA.toByte(), 0x00, 0x00, 0x00
                )
                val readRecordCommand: ApduCommand = byteArrayOf(
                    0x00, 0xB2.toByte(), 0x01, 0x0C, 0x10 // Read record 1 (16 bytes)
                )
                val getChallengeCommand: ApduCommand = byteArrayOf(
                    0x00, 0x84.toByte(), 0x00, 0x00, 0x08 // 8-byte challenge
                )
                val statusCommand: ApduCommand = byteArrayOf(
                    0x00, 0x22.toByte(), 0x00, 0x00, 0x00
                )
                val readPublicKeyCommand: ApduCommand = byteArrayOf(
                    0x00, 0x47.toByte(), 0x00, 0x00, 0x00
                )
                val readTransparentFileCommand: ApduCommand = byteArrayOf(
                    0x00, 0xB0.toByte(), 0x00, 0x00, 0x10 // Read 16 bytes
                )
                val getApplicationVersionCommand: ApduCommand = byteArrayOf(
                    0x80.toByte(), 0x70.toByte(), 0x00, 0x00, 0x00
                )
                val internalAuthenticationCommand: ApduCommand = byteArrayOf(
                    0x00, 0x88.toByte(), 0x00, 0x00, 0x08 // Length 8 bytes
                )
                val writeBinaryCommand: ApduCommand = byteArrayOf(
                    0x00, 0xD0.toByte(), 0x00, 0x00, 0x10 // Write 16 bytes
                )
                val authenticateCommand: ApduCommand = byteArrayOf(
                    0x00, 0x20.toByte(), 0x00, 0x80.toByte(), 0x08 // Verify PIN (8 bytes)
                )
                val selectApplicationCommand: ApduCommand = byteArrayOf(
                    0x00, 0xA4.toByte(), 0x04, 0x00, 0x0E, // AID length
                    0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31 // AID
                )
                val getResponseCommand: ApduCommand = byteArrayOf(
                    0x00, 0xC0.toByte(), 0x00, 0x00, 0x10 // Expecting 16 bytes response
                )

                // Add all commands to a list
                val commands: List<ApduCommand> = listOf(
                    selectFileCommand,
                    getCardUIDCommand,
                    getCardATRCommand,
                    readBinaryCommand,
                    getDataCommand,
                    readRecordCommand,
                    getChallengeCommand,
                    statusCommand,
                    readPublicKeyCommand,
                    readTransparentFileCommand,
                    getApplicationVersionCommand,
                    internalAuthenticationCommand,
                    writeBinaryCommand,
                    authenticateCommand,
                    selectApplicationCommand,
                    getResponseCommand
                )

                // Send each command and receive the response
                val responses = mutableListOf<String>()
                for (command in commands) {
                    try {
                        val response = isoDep.transceive(command)
                        // Convert response to hex string for better readability
                        val parsedResponse = response.joinToString(" ") { "%02X".format(it) }
                        responses.add(parsedResponse)
                    } catch (e: Exception) {
                        responses.add("Error with command: ${e.message}")
                    }
                }

                // Display all responses
                textView.text = responses.joinToString("\n")
                Log.d("APDU Responses", responses.joinToString("\n"))
                isoDep.close()
            } catch (e: Exception) {
                Toast.makeText(this, "Error reading card: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }

        fun parseApduResponse(response: ByteArray): String {
            val stringBuilder = StringBuilder()

            // Helper function to convert byte array to hex string
            fun ByteArray.toHexString(): String {
                return joinToString(" ") { "%02X".format(it) }
            }

            // Helper function to display the byte data in a readable way
            fun formatTable(title: String, data: String) {
                stringBuilder.append("**$title**\n")
                stringBuilder.append(data)
                stringBuilder.append("\n\n")
            }

            // Helper function to extract AID (if present)
            fun parseAID(data: ByteArray): String {
                return data.joinToString("") { "%02X".format(it) }
            }

            // Split the APDU response into sections
            var currentIndex = 0

            // Data Length (from the first byte)
            val dataLength = response[currentIndex++].toInt()
            formatTable("Data Length", "0x$dataLength")

            // Extracting AID if it exists (next bytes after the data length)
            val aidLength = response[currentIndex++].toInt()
            val aid = parseAID(response.copyOfRange(currentIndex, currentIndex + aidLength))
            currentIndex += aidLength
            formatTable("AID", aid)

            // Extract Application Name or Card Data (next part, may vary)
            val appDataLength = response[currentIndex++].toInt()
            val appData = response.copyOfRange(currentIndex, currentIndex + appDataLength).toHexString()
            currentIndex += appDataLength
            formatTable("Application Data", appData)

            // Check for any status code or control byte (typically the last bytes)
            val statusCode = response.takeLast(2).toHexString()
            formatTable("Status Word", statusCode)

            // Additional error processing (common errors)
            if (statusCode == "6D 00") {
                formatTable("Error", "Wrong Length or Incorrect Parameters")
            } else if (statusCode == "6A 86") {
                formatTable("Error", "File or Reference Data Not Found")
            }

            // Return the constructed table as a string
            return stringBuilder.toString()
        }
    }

}

private fun <E> List<E>.toHexString(): String {
    return joinToString(" ") { "%02X".format(it) }

}
