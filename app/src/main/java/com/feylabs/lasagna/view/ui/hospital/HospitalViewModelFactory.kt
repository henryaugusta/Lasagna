package com.feylabs.lasagna.view.ui.hospital

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.feylabs.lasagna.data.LasagnaRepository

class HospitalViewModelFactory constructor(val repository: LasagnaRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HospitalViewModel(repository) as T
    }
}
