package com.feylabs.lasagna.view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainMenuUserViewModel : ViewModel() {
    val title : MutableLiveData<String> = MutableLiveData()
}