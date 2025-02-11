package com.example.nfccardreader

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log
import android.widget.Toast

class CardEmulatorService : HostApduService() {

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        Log.d("Service", "started")
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
                Log.d("HCE Response", "Custom Command Received")
                val receivedMsg = extras?.getString("CUSTOM_MESSAGE") ?: "Default Response"

                receivedMsg.toByteArray() + "9000".hexStringToByteArray()
            }

            else -> {
                Log.d("HCE Response", "Command Not Recognized")
                "6A 82".hexStringToByteArray() // File Not Found
            }
        }
    }

    override fun onDeactivated(reason: Int) {
        Toast.makeText(this, "Service Deactivated", Toast.LENGTH_SHORT).show()
    }
}

// Utility function to convert a hex string to a byte array
private fun String.hexStringToByteArray(): ByteArray {
    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
}
