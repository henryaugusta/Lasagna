package com.bangkit.nadira.view.ui.weather

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.nadira.R
import com.bangkit.nadira.data.LasagnaRepository
import com.bangkit.nadira.data.model.api.Weather
import com.bangkit.nadira.data.remote.RemoteDataSource
import com.bangkit.nadira.databinding.ActivityWeatherBinding
import com.bangkit.nadira.util.Resource
import com.bangkit.nadira.util.baseclass.BaseActivity


class WeatherActivity : BaseActivity() {


    lateinit var viewModel: WeatherViewModel

    val vbind by lazy { ActivityWeatherBinding.inflate(layoutInflater) }

    val adapterSimple by lazy { WeatherAdapterSimple() }
    val adapterComplex by lazy { WeatherAdapterComplex() }

    val myArraySpinner: MutableList<String> = ArrayList()
    val myArrayMapper: MutableList<Int> = ArrayList()
    val mySpinner by lazy { vbind.spinner }


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

        viewModel = ViewModelProvider(this, factory).get(WeatherViewModel::class.java)


        myArraySpinner.add("Kepulauan Riau")
        myArrayMapper.add(501369)

        val spinnerArrayAdapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, myArraySpinner)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // The drop down vieww
        mySpinner.adapter = spinnerArrayAdapter

        viewModel.getCityList().observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    vbind.includeLoading.loadingRoot.setVisible()
                }
                is Resource.Error -> {
                    showSweetAlert("Error", it.message.toString(), R.color.colorRedPastel)
                    vbind.includeLoading.loadingRoot.setGone()
                }
                is Resource.Success -> {
                    myArrayMapper.clear()
                    myArraySpinner.clear()
                    myArraySpinner.add("Kepulauan Riau")
                    myArrayMapper.add(501369)
                    it.data?.forEach {

                        myArraySpinner.add(it.kota + "-" + it.propinsi)
                        myArrayMapper.add(it.id.toInt())
                    }
                    showSweetAlert("Success", "", R.color.xdGreen)
                }
                else -> {
                }
            }
        })

        vbind.srl.setOnRefreshListener {
            vbind.srl.isRefreshing = false
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


        setupInterface()


        vbind.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                vbind.spinner.selectedItem.toString().showLongToast()
                observeWeatherDetail(myArrayMapper[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }


        }

        observeWeatherDetail(501369)

    }

    private fun setupInterface() {
        adapterSimple.setInterface(object : WeatherAdapterSimple.WeatherAdapterInterface {
            override fun onclick(model: Weather.WeatherItem) {

            }
        })

        adapterComplex.setInterface(object : WeatherAdapterComplex.WeatherAdapterInterface {
            override fun onclick(model: Weather.WeatherItem) {

            }
        })

    }

    private fun observeWeatherDetail(id:Int) {
        viewModel.getDetailWeather(id.toString()).observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    vbind.includeLoading.loadingRoot.setVisible()
                }
                is Resource.Error -> {
                    showSweetAlert("Error", it.message.toString(), R.color.colorRedPastel)
                    vbind.includeLoading.loadingRoot.setGone()
                }
                is Resource.Success -> {
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