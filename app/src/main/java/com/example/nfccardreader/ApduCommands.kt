package com.example.nfccardreader

class ApduCommands {
    // List of APDU commands

    val selectFileCommand  = byteArrayOf(
        0x00,
        0xA4.toByte(),
        0x04,
        0x00,
        0x0E, // AID length
        0x32,
        0x50,
        0x41,
        0x59,
        0x2E,
        0x53,
        0x59,
        0x53,
        0x2E,
        0x44,
        0x44,
        0x46,
        0x30,
        0x31 // AID
    )
    val getCardUIDCommand = byteArrayOf(
        0xFF.toByte(), 0xCA.toByte(), 0x00, 0x00, 0x00
    )
    val getCardATRCommand = byteArrayOf(
        0x00, 0xC0.toByte(), 0x00, 0x00, 0x00
    )
    val readBinaryCommand = byteArrayOf(
        0x00, 0xB0.toByte(), 0x00, 0x00, 0x10 // Read 16 bytes
    )
    val getDataCommand = byteArrayOf(
        0x80.toByte(), 0xCA.toByte(), 0x00, 0x00, 0x00
    )
    val readRecordCommand = byteArrayOf(
        0x00, 0xB2.toByte(), 0x01, 0x0C, 0x10 // Read record 1 (16 bytes)
    )
    val getChallengeCommand = byteArrayOf(
        0x00, 0x84.toByte(), 0x00, 0x00, 0x08 // 8-byte challenge
    )
    val statusCommand = byteArrayOf(
        0x00, 0x22.toByte(), 0x00, 0x00, 0x00
    )
    val readPublicKeyCommand  = byteArrayOf(
        0x00, 0x47.toByte(), 0x00, 0x00, 0x00
    )
    val readTransparentFileCommand = byteArrayOf(
        0x00, 0xB0.toByte(), 0x00, 0x00, 0x10 // Read 16 bytes
    )
    val getApplicationVersionCommand = byteArrayOf(
        0x80.toByte(), 0x70.toByte(), 0x00, 0x00, 0x00
    )
    val internalAuthenticationCommand = byteArrayOf(
        0x00, 0x88.toByte(), 0x00, 0x00, 0x08 // Length 8 bytes
    )
    val writeBinaryCommand = byteArrayOf(
        0x00, 0xD0.toByte(), 0x00, 0x00, 0x10 // Write 16 bytes
    )
    val authenticateCommand = byteArrayOf(
        0x00, 0x20.toByte(), 0x00, 0x80.toByte(), 0x08 // Verify PIN (8 bytes)
    )
    val selectApplicationCommand = byteArrayOf(
        0x00,
        0xA4.toByte(),
        0x04,
        0x00,
        0x0E, // AID length
        0x32,
        0x50,
        0x41,
        0x59,
        0x2E,
        0x53,
        0x59,
        0x53,
        0x2E,
        0x44,
        0x44,
        0x46,
        0x30,
        0x31 // AID
    )
    val getResponseCommand = byteArrayOf(
        0x00, 0xC0.toByte(), 0x00, 0x00, 0x10 // Expecting 16 bytes response
    )

}

class cmds {
    private val apduCmd = ApduCommands()

    val commands: List<ByteArray> = listOf(
        apduCmd.selectFileCommand,
        apduCmd.getCardUIDCommand,
        apduCmd.getCardATRCommand,
        apduCmd.readBinaryCommand,
        apduCmd.getDataCommand,
        apduCmd.readRecordCommand,
        apduCmd.getChallengeCommand,
        apduCmd.statusCommand,
        apduCmd.readPublicKeyCommand,
        apduCmd.readTransparentFileCommand,
        apduCmd.getApplicationVersionCommand,
        apduCmd.internalAuthenticationCommand,
        apduCmd.writeBinaryCommand,
        apduCmd.authenticateCommand,
        apduCmd.selectApplicationCommand,
        apduCmd.getResponseCommand
    )
}