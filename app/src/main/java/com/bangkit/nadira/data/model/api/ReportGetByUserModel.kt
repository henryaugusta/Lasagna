package com.bangkit.nadira.data.model.api

import com.google.gson.annotations.SerializedName


data class ReportGetByUserModel(
    val http_response: Int,
    val message: String,
    val message_id: String,
    val report: Report,
    val status: Int
){
    data class Report(
        val current_page: Int,
        val data: MutableList<Data>,
        val first_page_url: String,
        val from: Int,
        val last_page: Int,
        val last_page_url: String,
        val links: List<Link>,
        val next_page_url: Any,
        val path: String,
        val per_page: Int,
        val prev_page_url: Any,
        val to: Int,
        val total: Int
    ){
        data class Link(
            val active: Boolean,
            val label: String,
            val url: Any
        )
        data class Data(
            val detail_alamat: String,
            val detail_kejadian: String,
            val id: Int,
            val id_category: Int,
            val id_people: Int,
            val id_staff: Any,
            val is_public: Int,
            val lat: String,
            val long: String,
            val no_laporan: String,
            val photo_path: String,
            val status: String,
            val status_desc: String,
            val status_label: String,
            val created_at: String,
            val updated_at: String,
            val staff: Staff,
            val people: People,
            val category: Category,
            @SerializedName("response")
            val responses: List<Response>
        ){
            data class Response(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("path")
                val path: String,
                @SerializedName("report_id")
                val reportId: Int,
                @SerializedName("responder")
                val responder: String,
                @SerializedName("response")
                val response: String,
                @SerializedName("status_code")
                val statusCode: String,
                @SerializedName("status_label")
                val statusLabel: String,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class People(
                val created_at: String,
                val email: String,
                val id: Int,
                val jk: Int,
                val nama: String,
                val nik: String,
                val no_telp: String,
                val photo_path: String,
                val random_time: Int,
                val updated_at: String,
                val username: String
            )
            data class Staff(
                val contact: String,
                val created_at: String,
                val email: String,
                val email_verified_at: Any,
                val id: Int,
                val name: String,
                val photo_path: String,
                val role: String,
                val updated_at: String
            )
            data class Category(
                val category_name: String,
                val created_at: String,
                val deleted_at: Any,
                val deleted_by: Any,
                val id: Int,
                val photo_path: String,
                val updated_at: String
            )
        }

    }
}