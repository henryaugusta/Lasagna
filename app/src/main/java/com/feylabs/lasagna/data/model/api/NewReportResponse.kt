package com.feylabs.lasagna.data.model.api


import com.google.gson.annotations.SerializedName

data class NewReportResponse(
    @SerializedName("http_response")
    val httpResponse: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("message_id")
    val messageId: String,
    @SerializedName("report")
    val report: Report,
    @SerializedName("status")
    val status: Int
) {
    data class Report(
        @SerializedName("current_page")
        val currentPage: Int,
        @SerializedName("data")
        val `data`: List<Data>,
        @SerializedName("first_page_url")
        val firstPageUrl: String,
        @SerializedName("from")
        val from: Int,
        @SerializedName("last_page")
        val lastPage: Int,
        @SerializedName("last_page_url")
        val lastPageUrl: String,
        @SerializedName("links")
        val links: List<Link>,
        @SerializedName("next_page_url")
        val nextPageUrl: Any,
        @SerializedName("path")
        val path: String,
        @SerializedName("per_page")
        val perPage: Int,
        @SerializedName("prev_page_url")
        val prevPageUrl: Any,
        @SerializedName("to")
        val to: Int,
        @SerializedName("total")
        val total: Int
    ) {
        data class Data(
            @SerializedName("category")
            val category: Category,
            @SerializedName("created_at")
            val createdAt: String,
            @SerializedName("detail_alamat")
            val detailAlamat: String,
            @SerializedName("detail_kejadian")
            val detailKejadian: String,
            @SerializedName("id")
            val id: Int,
            @SerializedName("id_category")
            val idCategory: Int,
            @SerializedName("id_people")
            val idPeople: Int,
            @SerializedName("id_staff")
            val idStaff: Any,
            @SerializedName("is_public")
            val isPublic: Int,
            @SerializedName("lat")
            val lat: String,
            @SerializedName("long")
            val long: String,
            @SerializedName("no_laporan")
            val noLaporan: String,
            @SerializedName("people")
            val people: People,
            @SerializedName("photo_path")
            val photoPath: String,
            @SerializedName("response")
            val response: List<Response>,
            @SerializedName("staff")
            val staff: Any,
            @SerializedName("status")
            val status: String,
            @SerializedName("status_desc")
            val statusDesc: String,
            @SerializedName("status_label")
            val statusLabel: String,
            @SerializedName("updated_at")
            val updatedAt: String
        ) {
            data class Category(
                @SerializedName("category_name")
                val categoryName: String,
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("deleted_at")
                val deletedAt: Any,
                @SerializedName("deleted_by")
                val deletedBy: Any,
                @SerializedName("id")
                val id: Int,
                @SerializedName("photo_path")
                val photoPath: String,
                @SerializedName("updated_at")
                val updatedAt: String
            )

            data class People(
                @SerializedName("created_at")
                val createdAt: String,
                @SerializedName("email")
                val email: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("jk")
                val jk: Int,
                @SerializedName("nama")
                val nama: String,
                @SerializedName("nik")
                val nik: String,
                @SerializedName("no_telp")
                val noTelp: String,
                @SerializedName("photo_path")
                val photoPath: String,
                @SerializedName("random_time")
                val randomTime: Int,
                @SerializedName("updated_at")
                val updatedAt: String,
                @SerializedName("username")
                val username: String
            )

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
        }

        data class Link(
            @SerializedName("active")
            val active: Boolean,
            @SerializedName("label")
            val label: String,
            @SerializedName("url")
            val url: Any
        )
    }
}