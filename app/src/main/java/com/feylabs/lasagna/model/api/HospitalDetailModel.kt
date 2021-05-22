package com.feylabs.lasagna.model.api


import com.google.gson.annotations.SerializedName

data class HospitalDetailModel(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("http_response")
    val httpResponse: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
){
    data class Data(
        @SerializedName("alamat")
        val alamat: String,
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("deskripsi")
        val deskripsi: String,
        @SerializedName("fasilitas")
        val fasilitas: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("kontak_ambulance")
        val kontakAmbulance: String,
        @SerializedName("kontak_rs")
        val kontakRs: String,
        @SerializedName("lat")
        val lat: String,
        @SerializedName("long")
        val long: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("operasional")
        val operasional: String,
        @SerializedName("photo_path")
        val photoPath: String,
        @SerializedName("real_photo_path")
        val realPhotoPath: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}