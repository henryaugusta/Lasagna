package com.feylabs.lasagna.util.networking

import android.util.Log
import java.io.File

object Endpoint {
    const val REAL_URL = "https://lapor-satgas.feylaboratory.xyz"
    const val BASE_URL = "https://lapor-satgas.feylaboratory.xyz/api"
//    const val REAL_URL = "http://192.168.1.161:9000"
//    const val BASE_URL = "http://192.168.1.161:9000/api"

    const val PEOPLE_LOGIN = "${BASE_URL}/people/login"
    const val PEOPLE_REGISTER = "${BASE_URL}/people/register"

    const val REPORT_SEND = "${BASE_URL}/report/store"
    fun REPORT_GET_BY_USER(id_user: String): String {
        return "${BASE_URL}/report/users/$id_user/"
    }

    fun REPORT_ALL(): String {
        return "${BASE_URL}/report/users/all"
    }
    fun REPORT_DELETE(id_report: String): String {
        return "${BASE_URL}/report/$id_report/delete"
    }
    fun REPORT_DETAIL(id_report: String): String {
        return "${BASE_URL}/report/$id_report/detail"
    }
    fun PEOPLE_DETAIL(id: String): String {
        return "${BASE_URL}/people/$id"
    }
    fun PEOPLE_DETAIL_UPDATE(id: String): String {
        return "${BASE_URL}/people/$id/update"
    }
    fun PEOPLE_UPDATE_PASSWORD(id: String): String {
        return "${BASE_URL}/people/$id/change-password"
    }




    fun PEOPLE_UPDATE_IMAGE(id: String): String {
//        http://127.0.0.1:9000/api/people/1/update_photo
        return "${BASE_URL}/people/$id/update_photo"
    }

    const val NEWS_FETCH_ALL = "${BASE_URL}/news/fetchAll"
    const val NEWS_STORE = "${BASE_URL}/news/store"

    const val HOSPITAL_FETCH = "${BASE_URL}/hospital/fetch"

    fun HOSPITAL_DELETE(id: String): String {
        return "${BASE_URL}/hospital/$id/delete"
    }

    fun HOSPITAL_SEND(): String {
        return "${BASE_URL}/hospital/store"
    }

    fun HOSPITAL_DETAIL(id:String): String {
        return "${BASE_URL}/hospital/$id/detail"
    }

    fun HOSPITAL_UPDATE(id:String): String {
        val url = "${BASE_URL}/hospital/$id/update"
        Log.d("endpoint",url)
        return url
    }

    fun GET_WEATHER(id:String): String {
        val url = "https://ibnux.github.io/BMKG-importer/cuaca/$id.json"
        Log.d("endpoint",url)
        return url
    }

    fun GET_CITY_WEATHER(): String {
        val url = "https://ibnux.github.io/BMKG-importer/cuaca/wilayah.json"
        Log.d("endpoint",url)
        return url
    }
    fun DELETE_CONTACT(id:String): String {
        val url = "$BASE_URL/contact/$id/delete"
        Log.d("endpoint",url)
        return url
    }
    const val GET_CONTACT = "${BASE_URL}/contact/fetch"
    const val CREATE_CONTACT = "${BASE_URL}/contact/store"

    const val CREATE_CATEGORY = "${BASE_URL}/category/store"
    const val GET_REPORT_CATEGORY = "${BASE_URL}/category"
    fun DELETE_CATEGORY(id:String): String {
        val url = "$BASE_URL/category/$id/delete"
        Log.d("endpoint",url)
        return url
    }

    fun EDIT_CATEGORY(id:String): String {
        val url = "$BASE_URL/category/$id/update"
        Log.d("endpoint",url)
        return url
    }

    fun STORE_RESPONSE(id:String): String {
        val url = "$BASE_URL/report/$id/store-response"
        Log.d("endpoint",url)
        return url
    }
}