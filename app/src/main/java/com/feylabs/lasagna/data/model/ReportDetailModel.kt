package com.feylabs.lasagna.data.model


import com.google.gson.annotations.SerializedName

data class ReportDetailModel(
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
        @SerializedName("kerusakan_bangunan")
        val kerusakanBangunan: String,
        @SerializedName("kerusakan_lain")
        val kerusakanLain: String,
        @SerializedName("kondisi_korban")
        val kondisiKorban: String,
        @SerializedName("korban_jiwa")
        val korbanJiwa: String,
        @SerializedName("lat")
        val lat: String,
        @SerializedName("long")
        val long: String,
        @SerializedName("no_laporan")
        val noLaporan: String,
        @SerializedName("people")
        val people: People,
        @SerializedName("peyebab_kejadian")
        val peyebabKejadian: String,
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
        @SerializedName("tanggal_kejadian")
        val tanggalKejadian: Any,
        @SerializedName("updated_at")
        val updatedAt: String,
        @SerializedName("waktu_kejadian")
        val waktuKejadian: String
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
}