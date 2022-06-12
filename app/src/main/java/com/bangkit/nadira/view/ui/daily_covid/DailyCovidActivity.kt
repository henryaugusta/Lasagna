package com.bangkit.nadira.view.ui.daily_covid

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bangkit.nadira.R
import com.bangkit.nadira.data.LasagnaRepository
import com.bangkit.nadira.data.remote.RemoteDataSource
import com.bangkit.nadira.databinding.ActivityDailyCovidBinding
import com.bangkit.nadira.util.Resource
import com.bangkit.nadira.util.baseclass.BaseActivity

class DailyCovidActivity :BaseActivity() {

    lateinit var viewModel: CovidViewModel

    val vbind by lazy { ActivityDailyCovidBinding.inflate(layoutInflater) }

    val adapterSimple by lazy { CovidAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vbind.root)


        vbind.labelBack.setOnClickListener {
            finish()
            onBackPressed()
        }

        vbind.srl.setOnRefreshListener {
            vbind.srl.isRefreshing=false
            observeCovid()

        }

        val factory = CovidViewModelFactory(LasagnaRepository((RemoteDataSource())))
        viewModel = ViewModelProvider(this, factory).get(CovidViewModel::class.java)
        vbind.includeLoading.loadingRoot.setVisible()

        vbind.rvCardDetail.apply {
            adapter = adapterSimple
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(this@DailyCovidActivity)
        }


        observeCovid()

    }

    private fun observeCovid() {
        viewModel.getCovid().observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    vbind.includeLoading.loadingRoot.setVisible()
                }
                is Resource.Error -> {
                    showSweetAlert("Error", it.message.toString(), R.color.colorRedPastel)
                    vbind.includeLoading.loadingRoot.setGone()
                }
                is Resource.Success -> {
                    vbind.includeLoading.loadingRoot.setGone()
                    it.data?.update?.harian?.toMutableList()?.let { it1 -> adapterSimple.setData(it1) }
                }
                else -> {
                }
            }
        })

    }
}