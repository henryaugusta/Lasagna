package com.bangkit.nadira.view.ui.hospital

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.nadira.data.LasagnaRepository

class HospitalViewModelFactory constructor(val repository: LasagnaRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HospitalViewModel(repository) as T
    }
}
