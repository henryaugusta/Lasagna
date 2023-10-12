package com.bangkit.nadira.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.bangkit.nadira.util.networking.Endpoint
import com.bangkit.nadira.util.networking.Endpoint.BASE_URL
import org.json.JSONObject
import timber.log.Timber

class MainMenuUserViewModel : ViewModel() {
    val title: MutableLiveData<String> = MutableLiveData()

    val doorState = MutableLiveData<Boolean>()
    val lampState = MutableLiveData<Boolean>()

    val doorStateStatus = MutableLiveData<String>()
    val doorStateMessage = MutableLiveData<String>()

    fun sendToken(token: String,device:String,name:String) {
        val endpoint = BASE_URL+"/store-fcm";
        val requestUrl = endpoint

        AndroidNetworking.post(requestUrl)
            .setPriority(Priority.MEDIUM)
            .addBodyParameter("username",name)
            .addBodyParameter("token",token)
            .addBodyParameter("device",device)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                }

                override fun onError(anError: ANError) {

                }
            })
    }


    fun randomUpdate() {
        val token = "oMps6Fj2XqUbsaLs1pCYbr6hl0j59I6x"
        val endpoints = listOf("V4", "V5", "V6", "V7", "V8")

        // Loop through each endpoint
        for (endpoint in endpoints) {
            // Generate a random value between 0 and 1 for each endpoint
            val randomValue = (0..1).random()

            // Construct the URL with the updated values
            val endpointUrl = "https://blynk.cloud/external/api/update?token=$token&$endpoint=$randomValue"

            AndroidNetworking.get(endpointUrl)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                    }

                    override fun onError(anError: ANError) {
                    }
                })
        }
    }

    fun checkSensors() {
        val token = "oMps6Fj2XqUbsaLs1pCYbr6hl0j59I6x"
        val endpoints = listOf("v4", "v5", "v6", "v7", "v8")

        val results = mutableListOf<Map<String, Any>>()

        // Counter to keep track of completed requests
        var completedRequests = 0

        endpoints.forEach { endpoint ->
            AndroidNetworking.get("https://blynk.cloud/external/api/get?token=$token&$endpoint")
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(object : StringRequestListener {
                    override fun onResponse(response: String?) {
                        val status = response ?: "" // Assuming 1 or 0 is returned as a string.

                        // Assuming 'status' is the key for the status value in the JSON response
                        val isOn = response.toString() == "1"

                        val result = mapOf(
                            "device" to endpoint,
                            "status" to status,
                            "is_on" to isOn
                        )

                        results.add(result)
                        Timber.d("168 sensors : $results")
                        // Check if all requests are completed
                        completedRequests++
                        if (completedRequests == endpoints.size) {
                            handleResults(results)
                        }
                    }

                    override fun onError(anError: ANError?) {
                        // Handle error if needed
                        // Note: You might want to provide appropriate error handling here
                        anError?.let {
                            // Handle error
                        }
                        // Increment the completed requests counter even if there's an error
                        completedRequests++
                        if (completedRequests == endpoints.size) {
                            handleResults(results)
                        }
                    }
                })
        }
    }

    fun handleResults(results: List<Map<String, Any>>) {
        val emergencyCount = results.count { it["status"] == "1" }
        val status = if (emergencyCount >= 3) {
            "EMERGENCY"
        } else {
            "OKAY"
        }
    }

    fun updateDoorState(isOpen: Boolean) {
        var endpoint = "";
        checkSensors()

        endpoint = if (isOpen) {
            "https://blynk.cloud/external/api/update?token=oMps6Fj2XqUbsaLs1pCYbr6hl0j59I6x&V9=1"
        } else {
            "https://blynk.cloud/external/api/update?token=oMps6Fj2XqUbsaLs1pCYbr6hl0j59I6x&V9=0"
        }
        val requestUrl = "$endpoint"

        AndroidNetworking.get(requestUrl)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    doorStateStatus.postValue("Pintu (Berhasil)")
                }

                override fun onError(anError: ANError) {
                    doorStateStatus.postValue("Pintu (Gagal)")
                    // Handle error, if needed
                }
            })
    }

    fun updateLampState(isOn: Boolean) {
        var endpoint = "";
        checkSensors()
        endpoint = if (isOn) {
            "https://blynk.cloud/external/api/update?token=oMps6Fj2XqUbsaLs1pCYbr6hl0j59I6x&V1=1"
        } else {
            "https://blynk.cloud/external/api/update?token=oMps6Fj2XqUbsaLs1pCYbr6hl0j59I6x&V1=0"
        }
        val requestUrl = "$endpoint"

        AndroidNetworking.get(requestUrl)
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                }

                override fun onError(anError: ANError) {
                }
            })
    }

}