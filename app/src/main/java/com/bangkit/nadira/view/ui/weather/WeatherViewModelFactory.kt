package com.bangkit.nadira.view.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.nadira.data.LasagnaRepository

class WeatherViewModelFactory constructor(val repository: LasagnaRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (WeatherViewModel(repository) as T)
    }
}
