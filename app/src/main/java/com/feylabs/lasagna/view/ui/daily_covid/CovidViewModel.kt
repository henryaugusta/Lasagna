package com.feylabs.lasagna.view.ui.daily_covid

import androidx.lifecycle.ViewModel
import com.feylabs.lasagna.data.LasagnaRepository

class CovidViewModel(val repository: LasagnaRepository) : ViewModel() {
    fun getCovid() = repository.getCovidDetail()
}