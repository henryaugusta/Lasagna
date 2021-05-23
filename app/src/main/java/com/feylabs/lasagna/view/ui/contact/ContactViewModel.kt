package com.feylabs.lasagna.view.ui.contact

import androidx.lifecycle.ViewModel
import com.feylabs.lasagna.data.LasagnaRepository
import java.io.File

class ContactViewModel(val repository: LasagnaRepository) : ViewModel() {
    fun getContact() = repository.getContact()
    fun storeContact(name: String, contact: String, photo: File) =
        repository.createContact(name, contact, photo)

    fun deleteContact(id:String) = repository.deleteContact(id)
}