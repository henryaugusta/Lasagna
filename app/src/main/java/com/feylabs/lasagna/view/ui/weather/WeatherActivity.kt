package com.feylabs.lasagna.view.ui.weather

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.feylabs.lasagna.R
import com.feylabs.lasagna.data.LasagnaRepository
import com.feylabs.lasagna.data.remote.RemoteDataSource
import com.feylabs.lasagna.databinding.ActivityWeatherBinding
import com.feylabs.lasagna.data.model.api.Weather
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.baseclass.BaseActivity

class WeatherActivity : BaseActivity() {


    lateinit var viewModel: WeatherViewModel

    val vbind by lazy { ActivityWeatherBinding.inflate(layoutInflater) }

    val adapterSimple by lazy { WeatherAdapterSimple() }
    val adapterComplex by lazy { WeatherAdapterComplex() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vbind.root)
        vbind.includeLoading.loadingRoot.setVisible()

        val factory = WeatherViewModelFactory(LasagnaRepository((RemoteDataSource())))

        vbind.rvCardSimple.apply {
            adapter = adapterSimple
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(this@WeatherActivity, LinearLayoutManager.HORIZONTAL, false)
        }

        vbind.srl.setOnRefreshListener {
            vbind.srl.isRefreshing=false
            viewModel.getDetailWeather()
        }
        vbind.labelBack.setOnClickListener {
            super.onBackPressed()
        }

        vbind.rvCardDetail.apply {
            adapter = adapterComplex
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(this@WeatherActivity)
        }


        adapterSimple.setInterface(object : WeatherAdapterSimple.WeatherAdapterInterface {
            override fun onclick(model: Weather.WeatherItem) {

            }
        })

        adapterComplex.setInterface(object : WeatherAdapterComplex.WeatherAdapterInterface {
            override fun onclick(model: Weather.WeatherItem) {

            }
        })


        viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)

        viewModel.getDetailWeather().observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    vbind.includeLoading.loadingRoot.setVisible()
                }
                is Resource.Error -> {
                    showSweetAlert("Error", it.message.toString(), R.color.colorRedPastel)
                    vbind.includeLoading.loadingRoot.setGone()
                }
                is Resource.Success -> {
                    showSweetAlert("Success", it.message.toString(), R.color.xdGreen)
                    it.data?.let { itu ->
                        adapterSimple.setData(itu)
                        adapterComplex.setData(itu)
                    }
                    adapterSimple.notifyDataSetChanged()
                    adapterComplex.notifyDataSetChanged()
                    vbind.includeLoading.loadingRoot.setGone()
                }
                else -> {
                }
            }

        })


    }
}