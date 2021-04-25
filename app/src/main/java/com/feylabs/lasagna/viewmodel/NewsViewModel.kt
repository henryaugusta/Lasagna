package com.feylabs.lasagna.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.feylabs.lasagna.model.ModelNewsCarousel
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.networking.Endpoint
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

class NewsViewModel : ViewModel() {
    val newsDB: MutableLiveData<Resource<MutableList<ModelNewsCarousel>>> = MutableLiveData()

    fun fetchNews() {
        newsDB.postValue(Resource.Loading())
        AndroidNetworking.get(Endpoint.NEWS_FETCH_ALL)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    if (response.getInt("size") > 0) {
                        val arrayNews = response.getJSONArray("news")
                        parseNewsJSON(arrayNews)
                    } else {
                        newsDB.postValue(Resource.Error("Tidak Ada Data"))
                    }
                }

                override fun onError(anError: ANError?) {
                    newsDB.postValue(Resource.Error("HTTP Connection Error"))
                    Timber.d("news: error -> ${anError?.message}")
                    Timber.d("news: error -> ${anError?.errorCode}")
                    Timber.d("news: error -> ${anError?.errorDetail}")
                    Timber.d("news: error -> ${anError?.errorBody}")
                }

            })
    }

    fun parseNewsJSON(arrayNews: JSONArray) {
        val tempMutableList = mutableListOf<ModelNewsCarousel>()
        for (i in 0 until arrayNews.length()) {
            val newsObject = arrayNews.getJSONObject(i)
            tempMutableList.add(
                ModelNewsCarousel(
                    id = newsObject.getString("id"),
                    link = newsObject.getString("title"),
                    photo_img = newsObject.getString("cover_link"),
                    content = newsObject.getString("content"),
                    title = newsObject.getString("title"),
                    author = newsObject.getString("author")
                )
            )
        }
        newsDB.postValue(Resource.Success(tempMutableList))
    }

}