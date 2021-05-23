package com.feylabs.lasagna.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.feylabs.lasagna.data.local.ContactItemModel
import com.feylabs.lasagna.data.remote.RemoteDataSource
import com.feylabs.lasagna.data.model.SendCreateHospitalModel
import com.feylabs.lasagna.data.model.api.*
import com.feylabs.lasagna.util.Resource
import java.io.File

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

    fun getCovidDetail(): MutableLiveData<Resource<GovCovidData>> {
        val apiResponse: MutableLiveData<Resource<GovCovidData>> = MutableLiveData()
        remoteDataSource.covidIDSummary(object : RemoteDataSource.CovidSummaryCallback {
            override fun callback(response: Resource<GovCovidData>) {
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

    fun getCityWeather(): MutableLiveData<Resource<CityWeather>> {
        val apiResponse: MutableLiveData<Resource<CityWeather>> = MutableLiveData()
        remoteDataSource.getCityWeather(object : RemoteDataSource.ListCityWeatherCallback {
            override fun callback(response: Resource<CityWeather>) {
                apiResponse.postValue(response)
            }
        })
        return apiResponse
    }

    fun createContact(name:String,description:String,photo:File): MutableLiveData<Resource<String>> {
        val apiResponse: MutableLiveData<Resource<String>> = MutableLiveData()
        remoteDataSource.createContact(name,description,photo,callback = object :RemoteDataSource.AddContactCallback{
            override fun callback(response: Resource<String>) {
                apiResponse.postValue(response)
            }
        })
        return apiResponse
    }

    fun deleteContact(id:String): MutableLiveData<Resource<String>> {
        val apiResponse: MutableLiveData<Resource<String>> = MutableLiveData()
        remoteDataSource.deleteContact(id,callback = object :RemoteDataSource.DeleteContactCallback{
            override fun callback(response: Resource<String>) {
                apiResponse.postValue(response)
            }
        })
        return apiResponse
    }

    fun getContact(): MutableLiveData<Resource<MutableList<ContactItemModel>>> {
        val apiResponse: MutableLiveData<Resource<MutableList<ContactItemModel>>> =
            MutableLiveData()
        val tempList = mutableListOf<ContactItemModel>()
        remoteDataSource.getContact(object : RemoteDataSource.GetContactCallback {
            override fun callback(response: Resource<ContactResponseModel>) {
                when (response) {
                    is Resource.Success -> {
                        response.data?.data?.forEach {
                            tempList.add(
                                ContactItemModel(
                                    createdAt = it.createdAt,
                                    id = it.id,
                                    deskripsi = it.deskripsi,
                                    name = it.name,
                                    photoPath = it.photoPath,
                                    updatedAt = it.updatedAt
                                )
                            )
                        }
                        apiResponse.postValue(Resource.Success(tempList,response.message))

                    }
                    is Resource.Loading -> {
                        apiResponse.postValue(Resource.Loading())
                    }
                    is Resource.Error -> {
                        apiResponse.postValue(Resource.Error(response.data?.message.toString()))
                    }
                }
            }

        })

        return apiResponse

    }


}