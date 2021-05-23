package com.feylabs.lasagna.data.model.api

data class AntaraNewsModel(
    val code: Int,
    val `data`: List<Data>,
    val messages: String,
    val status: String,
    val total: Int
){
    data class Data(
        val description: String,
        val image: String,
        val isoDate: String,
        val link: String,
        val title: String
    )
}