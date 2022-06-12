package com.bangkit.nadira.data.model.api

class CityWeather : ArrayList<CityWeather.CityWeatherItem>(){
    data class CityWeatherItem(
        val id: String,
        val kecamatan: String,
        val kota: String,
        val lat: String,
        val lon: String,
        val propinsi: String
    )
}