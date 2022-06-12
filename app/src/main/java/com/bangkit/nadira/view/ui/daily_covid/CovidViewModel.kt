package com.bangkit.nadira.view.ui.daily_covid

import androidx.lifecycle.ViewModel
import com.bangkit.nadira.data.LasagnaRepository

class CovidViewModel(val repository: LasagnaRepository) : ViewModel() {
    fun getCovid() = repository.getCovidDetail()
}