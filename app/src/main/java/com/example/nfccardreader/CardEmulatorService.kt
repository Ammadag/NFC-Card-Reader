package com.example.nfccardreader

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class CardEmulatorService : HostApduService() {

    private val command = CommandMap()
    private val cmdMap = command.commandResponseMap

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray? {
        // Convert the incoming APDU command to a hex string
        val apduHex = commandApdu.joinToString("") { "%02X".format(it) }
        Log.d("NFC_APDU", "Received Command: $apduHex")

        // Check if the received command matches the custom command
        if (commandApdu.contentEquals(byteArrayOf(0x00, 0xA5.toByte(), 0x00, 0x00, 0x04, 0x12, 0x34, 0x56, 0x78))) {
            Log.d("NFC_APDU", "Custom command received!")

            // Define the string response
            val responseString = "Hello NFC Reader"

            // Convert string to byte array
            val responseBytes = responseString.toByteArray(Charsets.UTF_8)

            // Append status word (SW1 SW2) for success (0x90 0x00)
            val finalResponse = responseBytes + byteArrayOf(0x90.toByte(), 0x00.toByte())

            Log.d("NFC_APDU", "Sending Response: ${finalResponse.joinToString("") { "%02X".format(it) }}")

            return finalResponse
        }

        // If the command is not recognized, return "Command Not Found" error
        return byteArrayOf(0x6A.toByte(), 0x82.toByte()) // SW1 SW2 for "File Not Found"
    }


    override fun onDeactivated(reason: Int) {
        // Handle card emulation deactivation if needed
    }

    // Utility function to convert hex string to byte array
    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
        }
        return data
    }
}
