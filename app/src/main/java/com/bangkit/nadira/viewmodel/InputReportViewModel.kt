package com.bangkit.nadira.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class InputReportViewModel : ViewModel() {

    val vmLat = MutableLiveData<Double>()
    val vmLong = MutableLiveData<Double>()
}