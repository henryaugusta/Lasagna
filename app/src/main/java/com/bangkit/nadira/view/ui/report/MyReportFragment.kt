package com.bangkit.nadira.view.ui.report

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bangkit.nadira.R
import com.bangkit.nadira.adapter.ReportAdapter
import com.bangkit.nadira.databinding.FragmentMyReportBinding
import com.bangkit.nadira.data.model.api.ReportGetByUserModel
import com.bangkit.nadira.util.Resource
import com.bangkit.nadira.util.SharedPreference.Preference
import com.bangkit.nadira.util.SharedPreference.const.USER_ID
import com.bangkit.nadira.util.baseclass.BaseFragment
import com.bangkit.nadira.viewmodel.MainMenuUserViewModel
import com.bangkit.nadira.view.bottom_sheet.OptionReportUserBottomSheet
import com.bangkit.nadira.view.ui.report.DetailReportActivity.Companion.REPORT_ID
import com.bangkit.nadira.viewmodel.UserReportViewModel
import timber.log.Timber

class MyReportFragment : BaseFragment() {


    val menuViewModel by lazy { ViewModelProvider(requireActivity()).get(MainMenuUserViewModel::class.java) }
    val userReportViewModel by lazy { ViewModelProvider(requireActivity()).get(UserReportViewModel::class.java) }
    val reportAdapter by lazy { ReportAdapter() }

    val optionUserSheet by lazy { OptionReportUserBottomSheet(requireActivity()) }


    lateinit var vbind: FragmentMyReportBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        menuViewModel.title.value = "My Emergency Report"
        userReportViewModel.getReportByUser(
            Preference(requireContext()).getPrefString(USER_ID).toString()
        )
        val root = inflater.inflate(R.layout.fragment_my_report, container, false)
        vbind = FragmentMyReportBinding.bind(root)
        return vbind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setUpRecylerView()
        setUpObserver()
        setUpAdapter()
        fetchData()

        vbind.srl.setOnRefreshListener {
            vbind.srl.isRefreshing = false
            fetchData()
        }

    }

    private fun fetchData() {
        userReportViewModel.getReportByUser(
            Preference(requireContext()).getPrefString(USER_ID).toString()
        )
    }

    private fun setUpAdapter() {
        reportAdapter.setInterface(object : ReportAdapter.ReportAdapterInterface {
            override fun onclick(model: ReportGetByUserModel.Report.Data) {

                optionUserSheet.show()

                optionUserSheet.myVbind.apply {
                    labelCategory.text = model.category.category_name
                    labelDetail.text = model.detail_kejadian
                    labelDetail.maxLines = 3
                    labelDetail.ellipsize = TextUtils.TruncateAt.MARQUEE
                    labelLocation.text = model.detail_alamat

                    btnSeeDetail.setOnClickListener {
                        "Lihat Detail".showLongToast()
                        startActivity(
                            Intent(requireContext(), DetailReportActivity::class.java)
                                .putExtra(REPORT_ID, model.id.toString())
                        )
                    }

                    btnCancelReport.setOnClickListener {
                        userReportViewModel.deleteReport(model.id.toString())
                        "Batalkan Laporan".showLongToast()
                        if (model.status.toInt() == 0) {
                            optionUserSheet.dismiss(true)
                        } else {
                            "Laporan Yang Sudah Diproses Tidak Dapat Dibatalkan".showLongToast()
                        }
                    }

                }
            }


        })
    }

    fun setAdapterData(datas: MutableList<ReportGetByUserModel.Report.Data>) {
        reportAdapter.setData(datas)
        reportAdapter.notifyDataSetChanged()
    }

    private fun setUpObserver() {
        userReportViewModel.reportByUser.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    Timber.d("myReportFragment: ->loading fetch report")
                    vbind.includeLoading.loadingRoot.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    vbind.includeLoading.loadingRoot.visibility = View.GONE
                    showSweetAlert("Error", it.message.toString(), R.color.xdRed)
                    Timber.d("myReportFragment: ->failed fetch report")
                    "Error".showLongToast()
                }
                is Resource.Success -> {
                    vbind.includeLoading.loadingRoot.visibility = View.GONE
                    Timber.d("myReportFragment: ->success fetch report")
                    setAdapterData(it.data?.report?.data!!)
                }
                else -> {
                }
            }
        })

        userReportViewModel.statusDeleteReport.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    Timber.d("myReportFragment: ->loading fetch report")
                    vbind.includeLoading.loadingRoot.visibility = View.VISIBLE
                    userReportViewModel.statusDeleteReport.postValue(Resource.Null())
                }
                is Resource.Error -> {
                    vbind.includeLoading.loadingRoot.visibility = View.GONE
                    showSweetAlert("Error", it.message.toString(), R.color.xdRed)
                    Timber.d("myReportFragment: ->failed fetch report")
                    "Gagal Menghapus Data".showLongToast()
                    userReportViewModel.statusDeleteReport.postValue(Resource.Null())
                }
                is Resource.Success -> {
                    vbind.includeLoading.loadingRoot.visibility = View.GONE
                    it.data?.showLongToast()
                    Timber.d("myReportFragment: ->success fetch report")
                    userReportViewModel.getReportByUser(
                        Preference(requireContext()).getPrefString(
                            USER_ID
                        ).toString()
                    )
                    optionUserSheet.dismiss(true)
                    userReportViewModel.statusDeleteReport.postValue(Resource.Null())
                }
                else -> {
                }
            }
        })
    }

    private fun setUpRecylerView() {
        vbind.rvReport.apply {
            adapter = reportAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }
    }


}