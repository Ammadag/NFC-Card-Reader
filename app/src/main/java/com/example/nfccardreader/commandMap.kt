package com.example.nfccardreader

class commandMap {
     val commandResponseMap = mapOf(
        byteArrayOf(
            0x00, 0xA4.toByte(), 0x04, 0x00, 0x0E, 0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53,
            0x2E, 0x44, 0x44, 0x46, 0x30, 0x31
        ) to hexStringToByteArray("6F35840E325041592E5359532E4444463031A523BF0C20611E4F07A000000004101050104465626974204D6173746572636172648701019000"),
        byteArrayOf(0xFF.toByte(), 0xCA.toByte(), 0x00, 0x00, 0x00) to hexStringToByteArray("6E00"),
        byteArrayOf(0x00, 0xC0.toByte(), 0x00, 0x00, 0x00) to hexStringToByteArray("6D00"),
        byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00, 0x10) to hexStringToByteArray("6D00"),
        byteArrayOf(0x80.toByte(), 0xCA.toByte(), 0x00, 0x00, 0x00) to hexStringToByteArray("6E00"),
        byteArrayOf(0x00, 0xB2.toByte(), 0x01, 0x0C, 0x10) to hexStringToByteArray("6D00"),
        byteArrayOf(0x00, 0x84.toByte(), 0x00, 0x00, 0x08) to hexStringToByteArray("6D00"),
        byteArrayOf(0x00, 0x22.toByte(), 0x00, 0x00, 0x00) to hexStringToByteArray("6D00"),
        byteArrayOf(0x00, 0x47.toByte(), 0x00, 0x00, 0x00) to hexStringToByteArray("6D00"),
        byteArrayOf(0x00, 0xB0.toByte(), 0x00, 0x00, 0x10) to hexStringToByteArray("6D00"),
        byteArrayOf(0x80.toByte(), 0x70.toByte(), 0x00, 0x00, 0x00) to hexStringToByteArray("6E00"),
        byteArrayOf(0x00, 0x88.toByte(), 0x00, 0x00, 0x08) to hexStringToByteArray("6D00"),
        byteArrayOf(0x00, 0xD0.toByte(), 0x00, 0x00, 0x10) to hexStringToByteArray("6D00"),
        byteArrayOf(0x00, 0x20.toByte(), 0x00, 0x80.toByte(), 0x08) to hexStringToByteArray("6D00"),
        byteArrayOf(
            0x00, 0xA4.toByte(), 0x04, 0x00, 0x0E, 0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53,
            0x2E, 0x44, 0x44, 0x46, 0x30, 0x31
        ) to hexStringToByteArray("6F35840E325041592E5359532E4444463031A523BF0C20611E4F07A000000004101050104465626974204D6173746572636172648701019000"),
        byteArrayOf(0x00, 0xC0.toByte(), 0x00, 0x00, 0x10) to hexStringToByteArray("6D00")
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