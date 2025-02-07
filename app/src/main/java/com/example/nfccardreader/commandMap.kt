package com.example.nfccardreader

class CommandMap {

    val commandResponseMap = mapOf(
        // Select AID command
        byteArrayOf(
            0x00, 0xA4.toByte(), 0x04, 0x00, 0x0E, // Select AID
            0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31
        ) to hexStringToByteArray("e3e6fbb"), // Replace this with the actual response byte array

        // Select Mastercard AID command
        byteArrayOf(
            0x00, 0xA4.toByte(), 0x04, 0x00, 0x07,
            0xA0.toByte(), 0x00, 0x00, 0x00, 0x04, 0x10, 0x10 // Mastercard AID
        ) to hexStringToByteArray("c42bcd8"), // Replace with response byte array

        // Get Processing Options command
        byteArrayOf(
            0x80.toByte(), 0xA8.toByte(), 0x00, 0x00, 0x04,
            0x83.toByte(), 0x02.toByte(), 0x80.toByte(), 0x00.toByte()
        ) to hexStringToByteArray("5046831"), // Response for Get Processing Options

        // Get Application Label command
        byteArrayOf(
            0x00, 0xCA.toByte(), 0x50.toByte(), 0x00.toByte(), 0x00
        ) to hexStringToByteArray("1857b16"), // Response for Get Application Label

        // Get Track 2 Data command
        byteArrayOf(
            0x00, 0xB2.toByte(), 0x01, 0x14, 0x00  // Read Record 1
        ) to hexStringToByteArray("a5a4c97"), // Response for Get Track 2 Data

        // Get Cardholder Name command
        byteArrayOf(
            0x00, 0xCA.toByte(), 0x5F.toByte(), 0x20.toByte(), 0x00
        ) to hexStringToByteArray("f92ce84"), // Response for Get Cardholder Name

        // Get Issuer Country Code command
        byteArrayOf(
            0x00, 0xCA.toByte(), 0x5F.toByte(), 0x28.toByte(), 0x00
        ) to hexStringToByteArray("a86566d"), // Response for Get Issuer Country Code

        // Get Service Code command
        byteArrayOf(
            0x00, 0xCA.toByte(), 0x5F.toByte(), 0x30.toByte(), 0x00
        ) to hexStringToByteArray("ed632a2"), // Response for Get Service Code

        // Get Issuer Public Key command
        byteArrayOf(
            0x00, 0xCA.toByte(), 0x9F.toByte(), 0x46.toByte(), 0x00
        ) to hexStringToByteArray("fed1b33") // Response for Get Issuer Public Key
    )

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
        }
        return data
    }
}
