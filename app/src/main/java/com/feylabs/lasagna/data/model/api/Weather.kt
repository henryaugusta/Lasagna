package com.feylabs.lasagna.data.model.api

class Weather : ArrayList<Weather.WeatherItem>(){
    data class WeatherItem(
        val cuaca: String,
        val humidity: String,
        val jamCuaca: String,
        val kodeCuaca: String,
        val tempC: String,
        val tempF: String
    )
}