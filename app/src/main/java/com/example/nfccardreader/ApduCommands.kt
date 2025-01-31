package com.example.nfccardreader

class ApduCommands {
    // List of APDU commands

    val selectAIDCommand = byteArrayOf(
        0x00, 0xA4.toByte(), 0x04, 0x00, 0x0E, // Select AID
        0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31
    )

    val SELECT_MASTER_CARD_AID = byteArrayOf(
        0x00, 0xA4.toByte(), 0x04, 0x00, 0x07,
        0xA0.toByte(), 0x00, 0x00, 0x00, 0x04, 0x10, 0x10 // Mastercard AID
    )

    val GET_PROCESSING_OPTIONS = byteArrayOf(
        0x80.toByte(), 0xA8.toByte(), 0x00, 0x00, 0x04,
        0x83.toByte(), 0x02.toByte(), 0x80.toByte(), 0x00.toByte()
    )

    val GET_APPLICATION_LABEL = byteArrayOf(
        0x00, 0xCA.toByte(), 0x50.toByte(), 0x00.toByte(), 0x00
    )

    val GET_TRACK_2_DATA = byteArrayOf(
        0x00, 0xB2.toByte(), 0x01, 0x14, 0x00  // Read Record 1
    )

    val GET_CARDHOLDER_NAME = byteArrayOf(
        0x00, 0xCA.toByte(), 0x5F.toByte(), 0x20.toByte(), 0x00
    )

    val GET_ISSUER_COUNTRY_CODE = byteArrayOf(
        0x00, 0xCA.toByte(), 0x5F.toByte(), 0x28.toByte(), 0x00
    )

    val GET_SERVICE_CODE = byteArrayOf(
        0x00, 0xCA.toByte(), 0x5F.toByte(), 0x30.toByte(), 0x00
    )

    val GET_ISSUER_PUBLIC_KEY = byteArrayOf(
        0x00, 0xCA.toByte(), 0x9F.toByte(), 0x46.toByte(), 0x00
    )

}

class cmds {
    private val apduCmd = ApduCommands()
    val commands: List<ByteArray> = listOf(


        apduCmd.selectAIDCommand,
        apduCmd.SELECT_MASTER_CARD_AID,
        apduCmd.GET_PROCESSING_OPTIONS,
        apduCmd.GET_APPLICATION_LABEL,
        apduCmd.GET_TRACK_2_DATA,
        apduCmd.GET_CARDHOLDER_NAME,
        apduCmd.GET_ISSUER_COUNTRY_CODE,
        apduCmd.GET_SERVICE_CODE,
        apduCmd.GET_ISSUER_PUBLIC_KEY,
    )

//    apduCmd.GET_DATA,
//    apduCmd.GET_RESPONSE,
//    apduCmd.READ_RECORD,
//    apduCmd.GET_PROCESSING_OPTIONS,
//    apduCmd.GENERATE_APPLICATION_CRYPTOGRAM,
//    apduCmd.SELECT_APPLICATION_AID

//    val commands: List<ByteArray> = listOf(
//        apduCmd.selectFileCommand,
//        apduCmd.getCardUIDCommand,
//        apduCmd.getCardATRCommand,
//        apduCmd.readBinaryCommand,
//        apduCmd.getDataCommand,
//        apduCmd.readRecordCommand,
//        apduCmd.getChallengeCommand,
//        apduCmd.statusCommand,
//        apduCmd.readPublicKeyCommand,
//        apduCmd.readTransparentFileCommand,
//        apduCmd.getApplicationVersionCommand,
//        apduCmd.internalAuthenticationCommand,
//        apduCmd.writeBinaryCommand,
//        apduCmd.authenticateCommand,
//        apduCmd.getResponseCommand
//    )
}


//    // List of APDU Commands
//    val SELECT_MASTER_AID = byteArrayOf(
//        0x00, 0xA4.toByte(), 0x04, 0x00, 0x07,
//        0xA0.toByte(), 0x00, 0x00, 0x00, 0x04, 0x10, 0x10,
//        0x00
//    )
//
//
//    val GET_PROCESSING_OPTIONS = byteArrayOf(
//        0x80.toByte(), 0xA8.toByte(), 0x00, 0x00, 0x02,
//        0x83.toByte(), 0x00
//    )
//
//    val READ_RECORD = byteArrayOf(
//        0x00, 0xB2.toByte(), 0x01, 0x0C, 0x00  // Read first record
//    )
//
//    val GENERATE_APPLICATION_CRYPTOGRAM = byteArrayOf(
//        0x80.toByte(), 0xAE.toByte(), 0x80.toByte(), 0x00, 0x00
//    )
//
//    val GET_DATA = byteArrayOf(
//        0x80.toByte(), 0xCA.toByte(), 0x00, 0x00, 0x00
//    )
//
//    val GET_RESPONSE = byteArrayOf(
//        0x00, 0xC0.toByte(), 0x00, 0x00, 0x10  // Expecting 16 bytes
//    )
// Command to check AID of the card
//    val SELECT_PPSE = byteArrayOf(
//        0x00, 0xA4.toByte(), 0x04, 0x00, 0x0E,
//        0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53,
//        0x2E, 0x44, 0x44, 0x46, 0x30, 0x31
//    )