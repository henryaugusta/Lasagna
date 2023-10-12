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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.nadira.R
import com.bangkit.nadira.adapter.ResponseAdapter
import com.bangkit.nadira.data.LasagnaRepository
import com.bangkit.nadira.data.model.api.MyNfcListResponse
import com.bangkit.nadira.data.remote.RemoteDataSource
import com.bangkit.nadira.databinding.ActivityMyNfcBinding
import com.bangkit.nadira.util.Resource
import com.bangkit.nadira.util.SharedPreference.Preference
import com.bangkit.nadira.util.SharedPreference.const
import com.bangkit.nadira.util.baseclass.BaseActivity
import com.bangkit.nadira.view.ui.hospital.HospitalViewModel
import com.bangkit.nadira.view.ui.hospital.HospitalViewModelFactory


class MyNfcActivity : BaseActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private val binding by lazy {
        ActivityMyNfcBinding.inflate(layoutInflater)
    }
    lateinit var viewmodel: NFCViewModel
    val adapterNFCList by lazy { MyNFCAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val factory = NFCViewModelFactory()
        viewmodel = ViewModelProvider(this, factory).get(NFCViewModel::class.java)

        val userId = Preference(this).getPrefString(const.USER_ID).toString()

        binding.rvMyNfc.apply {
            adapter = adapterNFCList
            layoutManager = LinearLayoutManager(this@MyNfcActivity)
        }

        adapterNFCList.setInterface(object : MyNFCAdapter.MyNfcListAdapterInterface {
            override fun onclick(model: MyNfcListResponse.MyNfcListResponseItem) {
                val alertDialogBuilder = AlertDialog.Builder(this@MyNfcActivity)
                alertDialogBuilder.setTitle("Confirm Delete")
                alertDialogBuilder.setMessage("Are you sure you want to delete this item?")

                alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->
                    // Perform the delete operation here
                    // You can call your delete method here or execute the delete logic
                    // Show a toast message after successful deletion
                    viewmodel.deleteCard(model.id.toString())
                    dialog.dismiss()
                }

                alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }

                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
        })

        binding.btnSaveCard.setOnClickListener {
            val cardId = binding.etCardId.text.toString()
            val cardLabel = binding.etCardLabel.text.toString()

            var isError=false;
            if (cardId.isEmpty()){
                isError=true;
            }

            if (cardLabel.isEmpty()){
                isError=true;
            }

            if (isError){
                showToast("Kartu Yang Ditambahkan Tidak Valid")
            }

            if (isError.not()){
                viewmodel.storeCard(
                    label = cardLabel,
                    cardId = cardId,
                    userId = userId
                )
            }
        }


        viewmodel.getMyCard(userId);

        viewmodel.myCardLiveData.observe(this, Observer {
            when (it) {
                is Resource.Error -> {
                    binding.includeLoading.loadingRoot.setGone()
                    showSweetAlert("Error", it.message.orEmpty(), R.color.colorRedPastel)
                }

                is Resource.Loading -> {
                    binding.includeLoading.loadingRoot.setVisible()
                    showToast("Memuat Kartu")
                }

                is Resource.Null -> {
                    binding.includeLoading.loadingRoot.setGone()
                }

                is Resource.Success -> {
                    binding.includeLoading.loadingRoot.setGone()
                    it.data?.let { it1 ->
                        adapterNFCList.setData(it1)
                        if (it1.isEmpty()) {
                            binding.cardListTitle.setGone();
                        }else{
                            binding.cardListTitle.setVisible();
                        }
                    }
                }
            }
        })


        viewmodel.addNewCardLiveData.observe(this, Observer {
            when (it) {
                is Resource.Error -> {
                    binding.includeLoading.loadingRoot.setGone()
                    showSweetAlert("Error", it.message.orEmpty(), R.color.colorRedPastel)
                }

                is Resource.Loading -> {
                    binding.includeLoading.loadingRoot.setVisible()
                    showToast("Menambahkan Kartu Baru")
                }

                is Resource.Null -> {
                    binding.includeLoading.loadingRoot.setGone()
                }

                is Resource.Success -> {
                    binding.includeLoading.loadingRoot.setGone()
                    viewmodel.getMyCard(userId)
                    showSweetAlert("Success", "Berhasil Menambahkan Kartu Baru", R.color.xdGreen)
                }
            }
        })


        viewmodel.deleteCardLiveData.observe(this, Observer {
            when (it) {
                is Resource.Error -> {
                    binding.includeLoading.loadingRoot.setGone()
                    showSweetAlert("Error", it.message.orEmpty(), R.color.colorRedPastel)
                }

                is Resource.Loading -> {
                    binding.includeLoading.loadingRoot.setVisible()
                }

                is Resource.Null -> {
                    binding.includeLoading.loadingRoot.setGone()
                }

                is Resource.Success -> {
                    showToast("Berhasil Menghapus Kartu")
                    viewmodel.getMyCard(userId)
                    binding.includeLoading.loadingRoot.setGone()
                }
            }

        })

        if (NfcAdapter.getDefaultAdapter(this) != null) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        }
        // Initialize NFC adapter

        if (nfcAdapter == null) {
            // NFC is not available on this device
            // You can display a message or show a fancy indicator here.
            showSweetAlert("Error", "NFC not available on your device", R.color.colorRedPastel)
            binding.titleCard.text = "NFC Tidak Tersedia Pada Device Anda"
        }

        // Check if NFC is enabled
        if (nfcAdapter?.isEnabled == true) {
            // NFC is available but not enabled; you can prompt the user to enable it here.
            showSweetAlert(
                "Error",
                "Enable NFC on your device and re-open the page",
                R.color.colorBlueWaves
            )
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
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

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
            binding.etCardId.setText(cardId.toString())
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