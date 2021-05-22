package com.feylabs.lasagna.view.ui.hospital

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.feylabs.lasagna.data.LasagnaRepository
import com.feylabs.lasagna.data.model.SendCreateHospitalModel
import com.feylabs.lasagna.data.model.api.HospitalModel
import com.google.android.gms.maps.model.LatLng

class HospitalViewModel(private val repository: LasagnaRepository) : ViewModel() {
    val vmLoc = MutableLiveData<LatLng>()
    val desc = MutableLiveData<String>()

    val hospitalModel = MutableLiveData<HospitalModel.Data> ()

    fun getHospital() = repository.getHospital()
    fun deleteHospital(id: String) = repository.deleteHospital(id)

    fun createHospital(model: SendCreateHospitalModel) = repository.createHospital(model)

    fun getHospitalDetail(id: String) = repository.getHospitalDetail(id)

    fun updateHospital(model: SendCreateHospitalModel) = repository.editHospital(model)

}