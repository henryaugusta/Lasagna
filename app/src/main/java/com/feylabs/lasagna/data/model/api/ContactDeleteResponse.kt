package com.feylabs.lasagna.data.model.api


import com.google.gson.annotations.SerializedName

data class ContactDeleteResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("http_response")
    val httpResponse: Int,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int
) {
    data class Data(
        @SerializedName("created_at")
        val createdAt: String,
        @SerializedName("deskripsi")
        val deskripsi: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String,
        @SerializedName("photo_path")
        val photoPath: String,
        @SerializedName("updated_at")
        val updatedAt: String
    )
}