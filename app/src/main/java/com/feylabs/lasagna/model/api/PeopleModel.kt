package com.feylabs.lasagna.model.api

data class PeopleModel(
    val id : String,
    val nama : String,
    val username : String,
    val email : String,
    val nik : String,
    val jk : String,
    val photo_path : String,
    val contact : String,
    val created_at : String,
    val updated_at : String
)