package com.bangkit.nadira.view.ui.weather

import androidx.lifecycle.ViewModel
import com.bangkit.nadira.data.LasagnaRepository

class WeatherViewModel(val repository: LasagnaRepository) : ViewModel() {

    fun getDetailWeather(id: String = "501369") = repository.getWeatherDetail(id.toInt())
    fun getCityList() = repository.getCityWeather()
}