package com.example.nfccardreader

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class CardEmulatorService : HostApduService() {

   private val command= commandMap()
    private val cmdMap = command.commandResponseMap


    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray? {
        // Check if the incoming command exists in the predefined map
        val apduHex = commandApdu.joinToString("") { "%02X".format(it) }
        Log.d("NFC_APDU", "Received Command: $apduHex")
        val response = cmdMap[commandApdu]
        if (response != null) {
            return response // Return the corresponding response
        }
        // If the command is not recognized, return "Unknown Command"
        return hexStringToByteArray("6A82") // Status word for "File Not Found"
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
