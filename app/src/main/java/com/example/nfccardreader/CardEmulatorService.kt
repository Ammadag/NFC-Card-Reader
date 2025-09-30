package com.example.nfccardreader

import android.nfc.cardemulation.HostApduService
import android.os.Bundle
import android.util.Log

class CardEmulatorService : HostApduService() {


    private val name = "Adeel Akhtar"
    private val accNo = "204474458"
    private val amount = "1"

    override fun processCommandApdu(commandApdu: ByteArray?, extras: Bundle?): ByteArray {
        if (commandApdu == null) return "6A82".hexStringToByteArray()

        return when {
            // AID Selection
            commandApdu.contentEquals(
                byteArrayOf(
                    0x00, 0xA4.toByte(), 0x04, 0x00, 0x07,
                    0xA0.toByte(), 0x00, 0x00, 0x00, 0x04, 0x10, 0x10,
                    0x00
                )
            ) -> {
                Log.d("HCE Response", "AID Selected")
                "9000".hexStringToByteArray() // Success Response
            }

            commandApdu.contentEquals(byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00, 0x10)) -> {
                name.toByteArray() + "9000".hexStringToByteArray()
            }

            commandApdu.contentEquals(byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x10, 0x10)) -> {
                accNo.toByteArray() + "9000".hexStringToByteArray()
            }

            commandApdu.contentEquals(byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x20, 0x10)) -> {
                amount.toByteArray() + "9000".hexStringToByteArray()
            }

            else -> {
                Log.w("HCE", "Unknown APDU")
                "6A82".hexStringToByteArray() // File not found
            }
        }
    }

    override fun onDeactivated(reason: Int) {
        Log.d("HCE", "Deactivated with reason $reason")
    }


    // Utility function to convert a hex string to a byte array
    private fun String.hexStringToByteArray(): ByteArray {
        return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }
}