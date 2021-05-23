package com.feylabs.lasagna.data.local


import com.google.gson.annotations.SerializedName

data class ContactItemModel(
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