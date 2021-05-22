package com.feylabs.lasagna.view.ui.weather

import androidx.lifecycle.ViewModel
import com.feylabs.lasagna.data.LasagnaRepository

class WeatherViewModel(val repository: LasagnaRepository) : ViewModel() {

    fun getDetailWeather(id: String = "501369") = repository.getWeatherDetail(id.toInt())
    fun getCityList() = repository.getCityWeather()
}