package com.feylabs.lasagna.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.feylabs.lasagna.data.model.ReportDetailModel
import com.feylabs.lasagna.data.model.api.ReportGetByUserModel
import com.feylabs.lasagna.data.model.api.SendReportModel
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.networking.Endpoint
import com.google.gson.Gson
import org.json.JSONObject
import timber.log.Timber
import java.io.File

class UserReportViewModel : ViewModel() {

    val sendReportStatus: MutableLiveData<Resource<String>> = MutableLiveData()
    val reportByUser: MutableLiveData<Resource<ReportGetByUserModel>> = MutableLiveData()
    val statusDeleteReport: MutableLiveData<Resource<String>> = MutableLiveData()
    val statusDetailReport: MutableLiveData<Resource<ReportDetailModel>> = MutableLiveData()
    val storeResponse: MutableLiveData<Resource<String>> = MutableLiveData()

    fun sendReportModel(model: SendReportModel) {
        sendReportStatus.postValue(Resource.Loading())
        model.apply {
            AndroidNetworking.upload(Endpoint.REPORT_SEND)
                .addMultipartParameter("id_people", id_people)
                .addMultipartParameter("id_category", id_category)
                .addMultipartParameter("detail_kejadian", detail_kejadian)
                .addMultipartParameter("detail_alamat", detail_alamat)
                .addMultipartParameter("lat", lat.toString())
                .addMultipartParameter("long", long.toString())
                .addMultipartParameter("is_public", is_public.toString())
                .addMultipartFile("photo", photo)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        Timber.d("uploadReport: response -> $response")
                        if (response.getInt("status") == 1) {
                            sendReportStatus.postValue(Resource.Success(response.getString("message")))
                        } else {
                            sendReportStatus.postValue(Resource.Error(response.getString("message")))
                        }
                    }

                    override fun onError(anError: ANError?) {
                        Timber.d("uploadReport: error -> ${anError?.message}")
                        Timber.d("uploadReport: error -> ${anError?.errorCode}")
                        Timber.d("uploadReport: error -> ${anError?.errorDetail}")
                        Timber.d("uploadReport: error -> ${anError?.errorBody}")
                        sendReportStatus.postValue(Resource.Error(anError?.localizedMessage.toString()))
                    }

                })
        }
    }

    fun sendReportResponse(
        id: String, status_code: String, responder: String,
        text: String,
        photo: File
    ) {
        storeResponse.postValue(Resource.Loading())
        AndroidNetworking.upload(Endpoint.STORE_RESPONSE(id))
            .addMultipartParameter("status_code", status_code)
            .addMultipartParameter("responder", responder)
            .addMultipartParameter("text", text)
            .addMultipartFile("photo", photo)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    Timber.d("uploadReport: response -> $response")
                    if (response.getInt("status") == 1) {
                        storeResponse.postValue(Resource.Success(response.getString("message")))
                    } else {
                        storeResponse.postValue(Resource.Error(response.getString("message")))
                    }
                }

                override fun onError(anError: ANError?) {
                    Timber.d("uploadReport: error -> ${anError?.message}")
                    Timber.d("uploadReport: error -> ${anError?.errorCode}")
                    Timber.d("uploadReport: error -> ${anError?.errorDetail}")
                    Timber.d("uploadReport: error -> ${anError?.errorBody}")
                    storeResponse.postValue(Resource.Error(anError?.localizedMessage.toString()))
                }

            })
    }

    fun deleteReport(reportID: String) {
        statusDeleteReport.value = Resource.Loading()
        AndroidNetworking.delete(Endpoint.REPORT_DELETE(reportID)).build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject?) {
                    if (response?.getInt("status") == 1) {
                        statusDeleteReport.value = Resource.Success("Success Menghapus Data")
                    } else {
                        statusDeleteReport.value =
                            Resource.Success(response?.getString("message").toString())
                    }
                }

                override fun onError(anError: ANError?) {
                    statusDeleteReport.value =
                        Resource.Success(anError?.localizedMessage.toString())
                }

            })
    }

    fun getReportByUser(userID: String) {
        val gson = Gson()
        reportByUser.value = Resource.Loading()
        AndroidNetworking.get(Endpoint.REPORT_GET_BY_USER(userID))
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    Timber.d("getReportByUser: success -> $response")
                    val reportResponse: ReportGetByUserModel =
                        gson.fromJson(response.toString(), ReportGetByUserModel::class.java)
                    if (reportResponse.report.total < 1) {
                        reportByUser.value = Resource.Error("Belum Ada Laporan")
                    } else {
                        reportByUser.value = Resource.Success(reportResponse)
                    }
                }

                override fun onError(anError: ANError?) {
                    reportByUser.value = Resource.Error(anError?.localizedMessage.toString())
                    Timber.d("getReportByUser: error -> ${anError?.message}")
                    Timber.d("getReportByUser: error -> ${anError?.errorCode}")
                    Timber.d("getReportByUser: error -> ${anError?.errorDetail}")
                    Timber.d("getReportByUser: error -> ${anError?.errorBody}")
                }

            })
    }


    fun getAllReport(filter: String = "") {
        val gson = Gson()
        reportByUser.value = Resource.Loading()
        AndroidNetworking.get(Endpoint.REPORT_ALL())
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    Timber.d("getReportByUser: success -> $response")
                    val reportResponse: ReportGetByUserModel =
                        gson.fromJson(response.toString(), ReportGetByUserModel::class.java)
                    if (reportResponse.report.total < 1) {
                        reportByUser.value = Resource.Error("Belum Ada Laporan")
                    } else {
                        reportByUser.value = Resource.Success(reportResponse)
                    }
                }

                override fun onError(anError: ANError?) {
                    reportByUser.value = Resource.Error(anError?.localizedMessage.toString())
                    Timber.d("getReportByUser: error -> ${anError?.message}")
                    Timber.d("getReportByUser: error -> ${anError?.errorCode}")
                    Timber.d("getReportByUser: error -> ${anError?.errorDetail}")
                    Timber.d("getReportByUser: error -> ${anError?.errorBody}")
                }

            })
    }

    fun getDetailReport(reportID: String) {
        val gson = Gson()
        statusDetailReport.value = Resource.Loading()
        AndroidNetworking.get(Endpoint.REPORT_DETAIL(reportID))
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String?) {
                    Timber.d("getReportDetail: success -> $response")
                    val reportResponse: ReportDetailModel =
                        gson.fromJson(response.toString(), ReportDetailModel::class.java)
                    if (reportResponse.status != 1) {
                        statusDetailReport.value = Resource.Error("Terjadi Kesalahan")
                    } else {
                        statusDetailReport.value = Resource.Success(reportResponse)
                    }

                }

                override fun onError(anError: ANError?) {
                    statusDetailReport.value = Resource.Error(anError?.localizedMessage.toString())
                    Timber.d("getReportDetail: error -> ${anError?.message}")
                    Timber.d("getReportDetail: error -> ${anError?.errorCode}")
                    Timber.d("getReportDetail: error -> ${anError?.errorDetail}")
                    Timber.d("getReportDetail: error -> ${anError?.errorBody}")
                }

            })
    }


}