package com.feylabs.lasagna.data.model

data class Report(
    val category: Category,
    val created_at: String,
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
    val people: People,
    val photo_path: String,
    val staff: Any,
    val status: String,
    val status_desc: String,
    val status_label: String,
    val updated_at: String
)