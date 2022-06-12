package com.bangkit.nadira.view.ui.daily_covid

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.nadira.data.LasagnaRepository

class CovidViewModelFactory constructor(val repository: LasagnaRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return (CovidViewModel(repository) as T)
    }
}
