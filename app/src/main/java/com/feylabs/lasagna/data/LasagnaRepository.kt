package com.feylabs.lasagna.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.feylabs.lasagna.data.remote.RemoteDataSource
import com.feylabs.lasagna.data.model.SendCreateHospitalModel
import com.feylabs.lasagna.data.model.api.DeleteHospitalModel
import com.feylabs.lasagna.data.model.api.HospitalModel
import com.feylabs.lasagna.data.model.api.Weather
import com.feylabs.lasagna.util.Resource

class LasagnaRepository(private val remoteDataSource: RemoteDataSource) {


    fun getHospital(): LiveData<Resource<List<HospitalModel.Data>>> {
        val apiResponse: MutableLiveData<Resource<List<HospitalModel.Data>>> = MutableLiveData()
        remoteDataSource.getHospital(object : RemoteDataSource.GetHospitalCallback {
            override fun callback(response: Resource<List<HospitalModel.Data>>) {
                apiResponse.value = response
            }
        })

        return apiResponse
    }

    fun deleteHospital(id: String): LiveData<Resource<DeleteHospitalModel>> {
        val apiResponse: MutableLiveData<Resource<DeleteHospitalModel>> = MutableLiveData()
        remoteDataSource.deleteHospital(id, object : RemoteDataSource.DeleteHospitalCallback {
            override fun callback(response: Resource<DeleteHospitalModel>) {
                apiResponse.value = response
            }

        })

        return apiResponse
    }

    fun getHospitalDetail(id: String): MutableLiveData<Resource<HospitalModel.Data>> {
        val apiResponse: MutableLiveData<Resource<HospitalModel.Data>> = MutableLiveData()

        remoteDataSource.getHospital(id, object : RemoteDataSource.DetailHospitalCallback {
            override fun callback(response: Resource<HospitalModel.Data>) {
                apiResponse.postValue(response)
            }
        })

        return apiResponse
    }

    fun createHospital(model: SendCreateHospitalModel): MutableLiveData<Resource<String>> {
        val apiResponse: MutableLiveData<Resource<String>> = MutableLiveData()
        remoteDataSource.addHospital(model, object : RemoteDataSource.AddHospitalCallback {
            override fun callback(response: Resource<String>) {
                apiResponse.postValue(response)
            }
        })
        return apiResponse
    }

    fun editHospital(model: SendCreateHospitalModel): MutableLiveData<Resource<String>> {
        val apiResponse: MutableLiveData<Resource<String>> = MutableLiveData()
        remoteDataSource.editHospital(model, object : RemoteDataSource.AddHospitalCallback {
            override fun callback(response: Resource<String>) {
                apiResponse.postValue(response)
            }
        })
        return apiResponse
    }

    fun getWeatherDetail(
        id: Int
    ): MutableLiveData<Resource<Weather>> {
        val apiResponse: MutableLiveData<Resource<Weather>> = MutableLiveData()

        remoteDataSource.getWeather(id, object : RemoteDataSource.DetailWeatherCallback {
            override fun callback(response: Resource<Weather>) {
                apiResponse.postValue(response)
            }
        })
        return apiResponse
    }


}