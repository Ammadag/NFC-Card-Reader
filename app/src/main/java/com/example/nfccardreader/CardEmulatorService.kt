package com.example.nfccardreader

import android.content.Intent
import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class CardEmulatorService : HostApduService() {

    private var message: String = "Default Message"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        message = intent?.getStringExtra("MESSAGE_KEY") ?: "Default Message"
        Log.d("HCE", "Received Message: $message")
        return START_STICKY
    }

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) {
            Log.e("HCE", "Received null APDU command")
            return "6F00".hexStringToByteArray() // Custom error response
        }

        val hexCommand = commandApdu.joinToString(" ") { "%02X".format(it) }
        Log.d("HCE Received Command", hexCommand)

        return when {
            // AID Selection
            commandApdu.contentEquals(
                byteArrayOf(
                    0x00, 0xA4.toByte(), 0x04, 0x00, 0x07,
                    0xA0.toByte(), 0x00, 0x00, 0x00, 0x04, 0x10, 0x10
                )
            ) -> {
                Log.d("HCE Response", "AID Selected")
                "9000".hexStringToByteArray() // Success Response
            }

            // Custom Command (e.g., 00 B0 00 00 10)
            commandApdu.contentEquals(byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00, 0x10)) -> {
                Log.d("HCE Response", "Sending Received Message: $message")
                message.toByteArray() + "9000".hexStringToByteArray() // Append success status
            }

            else -> {
                Log.d("HCE Response", "Command Not Recognized")
                "6A82".hexStringToByteArray() // File Not Found
            }
        }
    }

    override fun onDeactivated(reason: Int) {
        Log.d("HCE", "Service Deactivated: $reason")
    }
}

// Utility function to convert a hex string to a byte array
private fun String.hexStringToByteArray(): ByteArray {
    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}