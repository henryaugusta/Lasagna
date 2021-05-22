package com.feylabs.lasagna.data.model.api

data class HospitalModel(
    val `data`: List<Data>,
    val http_response: Int,
    val message: String,
    val status: Int
){
    data class Data(
        val alamat: String,
        val created_at: String,
        val deskripsi: String,
        val fasilitas: String,
        val id: Int,
        val kontak_ambulance: String,
        val kontak_rs: String,
        val lat: String,
        val long: String,
        val name: String,
        val operasional: String,
        val photo_path: String,
        val real_photo_path: String,
        val updated_at: String
    )
}