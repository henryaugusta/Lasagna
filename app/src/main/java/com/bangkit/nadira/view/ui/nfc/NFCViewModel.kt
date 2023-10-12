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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.bangkit.nadira.data.model.ModelNewsCarousel
import com.bangkit.nadira.data.model.api.MyNfcListResponse
import com.bangkit.nadira.data.model.api.ReportGetByUserModel
import com.bangkit.nadira.util.Resource
import com.bangkit.nadira.util.networking.Endpoint
import com.google.gson.Gson
import kotlinx.coroutines.flow.*
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.experimental.and

public class NFCViewModel() : ViewModel() {

    val addNewCardLiveData: MutableLiveData<Resource<String>> = MutableLiveData()
    val myCardLiveData: MutableLiveData<Resource<MyNfcListResponse>> = MutableLiveData()
    val deleteCardLiveData: MutableLiveData<Resource<String>> = MutableLiveData()

    fun getMyCard(userId: String) {
        addNewCardLiveData.postValue(Resource.Loading())
        AndroidNetworking.get(Endpoint.GET_MY_CARD + "/$userId")
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    try {
                        val myList =
                            Gson().fromJson(response.toString(), MyNfcListResponse::class.java)
                        myCardLiveData.postValue(Resource.Success(myList))
                    } catch (e: Exception) {
                        myCardLiveData.postValue(Resource.Error(e.toString()))
                    }
                }

                override fun onError(anError: ANError?) {
                    myCardLiveData.postValue(Resource.Error(anError.toString()))
                }
            })
    }

    fun deleteCard(cardId: String) {
        deleteCardLiveData.postValue(Resource.Loading())
        AndroidNetworking.get(Endpoint.DELETE_CARD + "/$cardId/delete")
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    deleteCardLiveData.postValue(Resource.Success(""))
                }

                override fun onError(anError: ANError?) {
                    deleteCardLiveData.postValue(Resource.Error(""))
                }
            })
    }

    fun storeCard(label:String,cardId: String,userId: String) {
        addNewCardLiveData.postValue(Resource.Loading())
        AndroidNetworking.post(Endpoint.NFC_STORE_NEW)
            .addBodyParameter("card_id",cardId)
            .addBodyParameter("label",label)
            .addBodyParameter("added_by",userId)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    addNewCardLiveData.postValue(Resource.Success(""))
                }

                override fun onError(anError: ANError?) {
                    addNewCardLiveData.postValue(Resource.Error(anError.toString()))
                }
            })
    }


}
