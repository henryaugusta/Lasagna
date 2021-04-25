package com.feylabs.lasagna.util.networking

object Endpoint {
//    const val BASE_URL = "https://lapor-satgas.feylaboratory.xyz/api"
//    const val BASE_URL = "http://127.0.0.1:9000/api"
    const val REAL_URL = "http://192.168.1.161:9000"
    const val BASE_URL = "http://192.168.1.161:9000/api"

    const val PEOPLE_LOGIN = "${BASE_URL}/people/login"
    const val PEOPLE_REGISTER = "${BASE_URL}/people/register"

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
}