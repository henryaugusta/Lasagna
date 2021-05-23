package com.feylabs.lasagna.view.ui.daily_covid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feylabs.lasagna.data.LasagnaRepository
import com.feylabs.lasagna.view.ui.hospital.HospitalViewModel

class CovidViewModelFactory constructor(val repository: LasagnaRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (CovidViewModel(repository) as T)
    }
}
