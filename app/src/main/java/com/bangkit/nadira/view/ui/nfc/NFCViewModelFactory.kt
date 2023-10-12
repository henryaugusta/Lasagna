package com.bangkit.nadira.view.ui.nfc

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class NFCViewModelFactory constructor() :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NFCViewModel() as T
    }
}
