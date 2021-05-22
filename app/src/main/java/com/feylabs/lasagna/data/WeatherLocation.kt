package com.feylabs.lasagna.data

class WeatherLocation : ArrayList<WeatherLocation.WeatherLocationItem>(){
    data class WeatherLocationItem(
        val id: String,
        val kecamatan: String,
        val kota: String,
        val lat: String,
        val lon: String,
        val propinsi: String
    )
}