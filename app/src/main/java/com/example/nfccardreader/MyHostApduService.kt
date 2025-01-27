package com.example.nfccardreader

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class CardEmulationService : HostApduService() {

    private var apduResponses: List<String>? = null

    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("CardData", MODE_PRIVATE)
        apduResponses = (0 until prefs.all.size).mapNotNull { prefs.getString(it.toString(), null) }
    }

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        Log.d("CardEmulationService", "Received APDU: ${commandApdu.joinToString(" ") { "%02X".format(it) }}")

        // Find and return a response based on the commandApdu
        // (For now, return the first stored response)
        return if (apduResponses != null && apduResponses!!.isNotEmpty()) {
            val response = apduResponses!![0].split(" ").map { it.toInt(16).toByte() }.toByteArray()
            response
        } else {
            // Return an empty response if no data is available
            byteArrayOf(0x6A.toByte(), 0x82.toByte()) // File not found
        }
    }

    override fun onDeactivated(reason: Int) {
        Log.d("CardEmulationService", "Card emulation deactivated: $reason")
    }
}
