package com.feylabs.lasagna.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainMenuUserViewModel : ViewModel() {
    val title : MutableLiveData<String> = MutableLiveData()
}