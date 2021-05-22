package com.feylabs.lasagna.view.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feylabs.lasagna.data.LasagnaRepository
import com.feylabs.lasagna.view.ui.hospital.HospitalViewModel

class WeatherViewModelFactory constructor(val repository: LasagnaRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (WeatherViewModel(repository) as T)
    }
}
