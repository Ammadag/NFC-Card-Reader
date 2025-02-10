package com.example.nfccardreader

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class CardEmulatorService : HostApduService() {

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) {
            Log.e("HCE", "Received null APDU command")
            return "6F00".hexStringToByteArray() // Custom unknown error response
        }

        val hexCommand = commandApdu.joinToString(" ") { "%02X".format(it) }
        Log.d("HCE Received Command", hexCommand)

        return when {
            commandApdu.contentEquals(
                byteArrayOf(
                    0x00, 0xA4.toByte(), 0x04, 0x00, 0x07,
                    0xA0.toByte(), 0x00, 0x00, 0x00, 0x04, 0x10, 0x10
                )
            ) -> {
                Log.d("HCE Response", "Valid AID selected")
                "9000".hexStringToByteArray() // Success Response
            }

            else -> {
                Log.d("HCE Response", "Command not recognized")
                "6A82".hexStringToByteArray() // File not found
            }
        }
    }

    override fun onDeactivated(reason: Int) {
        Log.d("HCE", "Card Emulation Deactivated: Reason $reason")
    }
}

// Extension function to convert hex string to byte array
fun String.hexStringToByteArray(): ByteArray {
    val cleanedHex = this.replace("\\s".toRegex(), "") // Remove spaces if any
    require(cleanedHex.length % 2 == 0) { "Invalid hex string length" }

    return ByteArray(cleanedHex.length / 2) { i ->
        cleanedHex.substring(i * 2, i * 2 + 2).toInt(16).toByte()
    }
}
