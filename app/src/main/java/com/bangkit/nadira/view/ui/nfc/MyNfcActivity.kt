package com.bangkit.nadira.view.ui.nfc


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.pm.PackageInfoCompat
import com.bangkit.nadira.R
import com.bangkit.nadira.databinding.ActivityMyNfcBinding
import com.bangkit.nadira.util.baseclass.BaseActivity



class MyNfcActivity : BaseActivity() {

    private lateinit var cardIdTextView: TextView
    private var nfcAdapter: NfcAdapter? = null
    private val binding by lazy {
        ActivityMyNfcBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        cardIdTextView = binding.cardIdTextView

        if(NfcAdapter.getDefaultAdapter(this)!=null){
            nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        }
        // Initialize NFC adapter

        if (nfcAdapter == null) {
            // NFC is not available on this device
            // You can display a message or show a fancy indicator here.
            showSweetAlert("Error", "NFC not available on your device", R.color.colorRedPastel)
            binding.titleCard.text="NFC Tidak Tersedia Pada Device Anda"
        }

        // Check if NFC is enabled
        if (nfcAdapter?.isEnabled == true) {
            // NFC is available but not enabled; you can prompt the user to enable it here.
            showSweetAlert("Error", "Enable NFC on your device and re-open the page", R.color.colorBlueWaves)
        }

        // Check and request NFC permission if not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.NFC)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.NFC), 1
            )
        }

    }

    // Handle permission request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            // Handle the case where NFC permission is denied
            // You may want to inform the user or disable NFC-related functionality
        }
    }

    override fun onResume() {
        super.onResume()

        // Enable NFC foreground dispatch to capture card ID
        val intent = Intent(this, this::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        nfcAdapter?.enableForegroundDispatch(
            this,
            pendingIntent,
            null,
            arrayOf(arrayOf(NfcA::class.java.name))
        )
    }

    override fun onPause() {
        super.onPause()

        // Disable NFC foreground dispatch
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val cardId = bytesToHexString(tag?.id)

            // Update the UI with the card ID
            cardIdTextView.text = "Card ID: $cardId"
        }
    }

    private fun bytesToHexString(bytes: ByteArray?): String {
        val sb = StringBuilder()
        for (b in bytes ?: return "") {
            sb.append(String.format("%02X", b))
        }
        return sb.toString()
    }
}