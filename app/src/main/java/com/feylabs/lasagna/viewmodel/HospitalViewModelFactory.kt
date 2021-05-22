package com.feylabs.lasagna.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feylabs.lasagna.data.LasagnaRepository
import com.feylabs.lasagna.view.ui.hospital.HospitalViewModel

class HospitalViewModelFactory constructor(val repository: LasagnaRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HospitalViewModel(repository) as T
    }
}
