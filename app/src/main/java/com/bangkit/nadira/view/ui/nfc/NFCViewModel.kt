package com.bangkit.nadira.view.ui.nfc

import android.app.Application
import android.content.ContentValues
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.MifareUltralight
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.experimental.and

public class NFCViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private val TAG = NFCViewModel::class.java.simpleName
        private const val prefix = "android.nfc.tech."
    }

    private val liveNFC = MutableStateFlow<NFCStatus?>(null)
    private val liveToast = MutableSharedFlow<String?>()
    private val liveTag = MutableStateFlow<String?>(null)

    // Toast Methods
    private fun updateToast(message: String) {
        liveToast.tryEmit(message)
    }

    fun observeToast(): SharedFlow<String?> {
        return liveToast.asSharedFlow()
    }

    // NFC Methods
    fun getNFCFlags(): Int {
        return NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V or
                NfcAdapter.FLAG_READER_NFC_BARCODE
    }

    fun getExtras(): Bundle {
        val options: Bundle = Bundle()
        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 30000)
        return options
    }

    fun onCheckNFC(isChecked: Boolean) {
        Log.d(TAG, "onCheckNFC($isChecked)")
        if (isChecked) {
            postNFCStatus(NFCStatus.Tap)
        } else {
            postNFCStatus(NFCStatus.NoOperation)
//            postToast("NFC is Disabled, Please Toggle On!")
        }
    }

    fun readTag(tag: Tag?) {
        Log.d(TAG, "readTag($tag ${tag?.techList?.contentToString()})")
        postNFCStatus(NFCStatus.Process)
        val stringBuilder: StringBuilder = StringBuilder()
        val id: ByteArray? = tag?.id
        stringBuilder.append("Tag ID (hex): ${getHex(id!!)} \n")
        stringBuilder.append("Tag ID (dec): ${getDec(id)} \n")
        stringBuilder.append("Tag ID (reversed): ${getReversed(id)} \n")
        stringBuilder.append("Technologies: ")
        tag?.techList?.forEach { tech ->
            stringBuilder.append(tech.substring(prefix.length))
            stringBuilder.append(", ")
        }
        stringBuilder.delete(stringBuilder.length - 2, stringBuilder.length)
        tag?.techList?.forEach { tech ->
            if (tech == MifareClassic::class.java.name) {
                stringBuilder.append('\n')
                val mifareTag: MifareClassic = MifareClassic.get(tag)
                val type: String
                if (mifareTag.type == MifareClassic.TYPE_CLASSIC) type = "Classic"
                else if (mifareTag.type == MifareClassic.TYPE_PLUS) type = "Plus"
                else if (mifareTag.type == MifareClassic.TYPE_PRO) type = "Pro"
                else type = "Unknown"
                stringBuilder.append("Mifare Classic type: $type \n")
                stringBuilder.append("Mifare size: ${mifareTag.size} bytes \n")
                stringBuilder.append("Mifare sectors: ${mifareTag.sectorCount} \n")
                stringBuilder.append("Mifare blocks: ${mifareTag.blockCount}")
            }
            if (tech == MifareUltralight::class.java.name) {
                stringBuilder.append('\n')
                val mifareUlTag: MifareUltralight = MifareUltralight.get(tag)
                val type: String
                if (mifareUlTag.type == MifareUltralight.TYPE_ULTRALIGHT) type =
                    "Ultralight"
                else if (mifareUlTag.type == MifareUltralight.TYPE_ULTRALIGHT_C) type =
                    "Ultralight C"
                else type = "Unknown"
                stringBuilder.append("Mifare Ultralight type: ")
                stringBuilder.append(type)
            }
        }
        Log.d(TAG, "Datum: $stringBuilder")
        Log.d(ContentValues.TAG, "dumpTagData Return \n $stringBuilder")
        postNFCStatus(NFCStatus.Read)
        liveTag.tryEmit("${getDateTimeNow()} \n ${stringBuilder}")
    }

    fun updateNFCStatus(status: NFCStatus) {
        postNFCStatus(status)
    }

    private fun postNFCStatus(status: NFCStatus) {
        Log.d(TAG, "postNFCStatus($status)")
        if (NFCManager.isSupportedAndEnabled(getApplication())) {
            liveNFC.tryEmit(status)
        } else if (NFCManager.isNotEnabled(getApplication())) {
            liveNFC.tryEmit(NFCStatus.NotEnabled)
//            postToast("Please Enable your NFC!")
            liveTag.tryEmit("Please Enable your NFC!")
        } else if (NFCManager.isNotSupported(getApplication())) {
            liveNFC.tryEmit(NFCStatus.NotSupported)
//            postToast("NFC Not Supported!")
            liveTag.tryEmit("NFC Not Supported!")
        }
        if (NFCManager.isSupportedAndEnabled(getApplication()) && status == NFCStatus.Tap) {
            liveTag.tryEmit("Please Tap Now!")
        } else {
            liveTag.tryEmit(null)
        }
    }

    fun observeNFCStatus(): StateFlow<NFCStatus?> {
        return liveNFC.asStateFlow()
    }

    // Tags Information Methods
    private fun getDateTimeNow(): String {
        Log.d(TAG, "getDateTimeNow()")
        val TIME_FORMAT: DateFormat = SimpleDateFormat.getDateTimeInstance()
        val now: Date = Date()
        Log.d(ContentValues.TAG, "getDateTimeNow() Return ${TIME_FORMAT.format(now)}")
        return TIME_FORMAT.format(now)
    }

    private fun getHex(bytes: ByteArray): String {
        val sb = StringBuilder()
        for (i in bytes.indices.reversed()) {
            val b: Int = bytes[i].and(0xff.toByte()).toInt()
            if (b < 0x10) sb.append('0')
            sb.append(Integer.toHexString(b))
            if (i > 0)
                sb.append(" ")
        }
        return sb.toString()
    }

    private fun getDec(bytes: ByteArray): Long {
        Log.d(TAG, "getDec()")
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices) {
            val value: Long = bytes[i].and(0xffL.toByte()).toLong()
            result += value * factor
            factor *= 256L
        }
        return result
    }

    private fun getReversed(bytes: ByteArray): Long {
        Log.d(TAG, "getReversed()")
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices.reversed()) {
            val value = bytes[i].and(0xffL.toByte()).toLong()
            result += value * factor
            factor *= 256L
        }
        return result
    }

    fun observeTag(): StateFlow<String?> {
        return liveTag.asStateFlow()
    }
}
