package com.feylabs.lasagna.view.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.feylabs.lasagna.R
import com.feylabs.lasagna.view.ui.weather.WeatherActivity
import com.feylabs.lasagna.adapter.NewsAdapterCarousel
import com.feylabs.lasagna.adapter.ReportCategoryAdapter
import com.feylabs.lasagna.data.LasagnaRepository
import com.feylabs.lasagna.databinding.FragmentHomeBinding
import com.feylabs.lasagna.databinding.ItemCategoryReportBinding
import com.feylabs.lasagna.databinding.LayoutDetailNewsBinding
import com.feylabs.lasagna.data.model.ModelNewsCarousel
import com.feylabs.lasagna.data.model.api.ReportCategoryModel
import com.feylabs.lasagna.data.remote.RemoteDataSource
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.URL
import com.feylabs.lasagna.util.baseclass.BaseFragment
import com.feylabs.lasagna.util.baseclass.Util
import com.feylabs.lasagna.viewmodel.MainMenuUserViewModel
import com.feylabs.lasagna.view.bottom_sheet.NewsBottomSheet
import com.feylabs.lasagna.view.ui.daily_covid.DailyCovidActivity
import com.feylabs.lasagna.view.ui.hospital.ListHospitalActivity
import com.feylabs.lasagna.view.ui.proceed.ListReportActivity
import com.feylabs.lasagna.view.ui.send_report.UserInputReportActivity
import com.feylabs.lasagna.viewmodel.NewsViewModel
import com.squareup.picasso.Picasso
import timber.log.Timber


class HomeFragment : BaseFragment() {

    private lateinit var homeViewModel: HomeViewModel

    val newsViewModel by lazy { ViewModelProvider(requireActivity()).get(NewsViewModel::class.java) }
    val menuViewModel by lazy { ViewModelProvider(requireActivity()).get(MainMenuUserViewModel::class.java) }
    val menuAdapter by lazy { ReportCategoryAdapter() }


    lateinit var vbinding: FragmentHomeBinding
    lateinit var newsBottomSheetBinding: LayoutDetailNewsBinding

    lateinit var newsAdapterCarousel: NewsAdapterCarousel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bottomNews = inflater.inflate(R.layout.layout_detail_news, container, false)
        newsBottomSheetBinding = LayoutDetailNewsBinding.bind(bottomNews)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        vbinding = FragmentHomeBinding.bind(root)
        return vbinding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Util.setStatusBarLight(requireActivity())

        val factory = HomeViewModelFactory(LasagnaRepository(RemoteDataSource()))
        homeViewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

        menuViewModel.title.value = "Beranda"
        newsViewModel.fetchNews()



        setUpAdapter()
        setupRecyclerView()

        vbinding.indicator.attachToRecyclerView(vbinding.containerCardNews)

        setUpViewAction()
        setUpPrixaArtificalIntelligence()
        setupObserver()

        setUpMenuRecyclerview()
        populateMenuRecyclerview()

    }

    private fun populateMenuRecyclerview() {

        menuAdapter.setData(
            mutableListOf(
                ReportCategoryModel.Data(
                    category_name = "Rumah Sakit",
                    id = 1,
                    photo_path = "/static_web_files/hospital.png"
                ),
                ReportCategoryModel.Data(
                    category_name = "Laporan Warga",
                    id = 2,
                    photo_path = "/static_web_files/report.png"
                )
//                ReportCategoryModel.Data(
//                    category_name = "Prediksi Cuaca",
//                    id = 3,
//                    photo_path = "/static_web_files/weather.png"
//                ),
//                ReportCategoryModel.Data(
//                    category_name = "Covid Harian",
//                    id = 4,
//                    photo_path = "/static_web_files/covid.png"
//                ),
//                ReportCategoryModel.Data(
//                    category_name = "Kontak Penting",
//                    id = 5,
//                    photo_path = "/static_web_files/contact.png"
//                )
            )
        )

        menuAdapter.setInterface(object : ReportCategoryAdapter.MyCategoryInterface {
            override fun onclick(
                model: ReportCategoryModel.Data,
                adaptVbind: ItemCategoryReportBinding
            ) {
                when (model.id) {
                    1 -> {
                        startActivity(Intent(requireContext(), ListHospitalActivity::class.java))
                    }
                    2 -> {
                        startActivity(Intent(requireContext(), ListReportActivity::class.java))
                    }
                    3 -> {
                        startActivity(Intent(requireContext(), WeatherActivity::class.java))
                    }
                    4 -> {
                        startActivity(Intent(requireContext(), DailyCovidActivity::class.java))
                    }
                    5 -> {
                        Navigation.findNavController(vbinding.root)
                            .navigate(R.id.action_navigation_home_to_contactListFragment)
                    }
                }

            }

        })

        menuAdapter.notifyDataSetChanged()
    }

    private fun setUpMenuRecyclerview() {
        vbinding.rvCategory.apply {
            adapter = menuAdapter
            layoutManager = GridLayoutManager(requireContext(), 4)
        }
    }


    private fun setUpAdapter() {
        newsAdapterCarousel = NewsAdapterCarousel()

        //Set News Item On Click
        newsAdapterCarousel.setInterface(object : NewsAdapterCarousel.NewsAdapterInterface {
            override fun onclick(model: ModelNewsCarousel) {
                model.title.showLongToast()
                val motDetailBottomSheet = NewsBottomSheet(requireActivity())
                motDetailBottomSheet.apply {
                    val closeBtn = findViewById<ImageButton>(R.id.btn_close_detail_news)
                    val content = findViewById(R.id.tv_detail_news_content) as TextView
                    val title = findViewById(R.id.tv_det_news_title) as TextView
                    val author = findViewById(R.id.tv_detail_news_author) as TextView
                    val image = findViewById(R.id.iv_det_news_img) as ImageView
                    title.text = model.title
                    author.text = model.author

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        content.text = (Html.fromHtml(model.content, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        content.text = (Html.fromHtml(model.content));
                    }

                    closeBtn.setOnClickListener {
                        this.dismiss(true)
                    }
                    Picasso.get()
                        .load(model.photo_img)
                        .into(image)
                }

                motDetailBottomSheet.show(true)
            }

        })

    }

    private fun setupRecyclerView() {
        vbinding.containerCardNews.adapter = newsAdapterCarousel
        vbinding.containerCardNews.apply {
            setHasFixedSize(true)
            adapter = newsAdapterCarousel
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupObserver() {
        newsViewModel.newsDB.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    Timber.d("register: loading")
                    vbinding.includeLoading.loadingRoot.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    vbinding.includeLoading.loadingRoot.visibility = View.GONE
                    Timber.d("register: error")
                    "Gagal Terhubung Dengan Server".showLongToast()
                }
                is Resource.Success -> {
                    it.data?.let { it1 -> newsAdapterCarousel.setData(it1) }
                    newsAdapterCarousel.notifyDataSetChanged()
                    vbinding.includeLoading.loadingRoot.visibility = View.GONE
                    Timber.d("register: success")
                }

            }
        })


        /*
             homeViewModel.covidSummary.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Success -> {
                    it.data.apply {
                        vbinding.odp.text = "Total ODP ( Indonesia ) : "+this?.data?.jumlahOdp.toString()
                        vbinding.death.text = this?.update?.total?.jumlahMeninggal.toString()
                        vbinding.deathNew.text =
                            this?.update?.penambahan?.jumlahMeninggal.toString()
                        vbinding.recovered.text = this?.update?.total?.jumlahMeninggal.toString()
                        vbinding.recoveredNew.text =
                            this?.update?.penambahan?.jumlahMeninggal.toString()
                        vbinding.infected.text = this?.update?.total?.jumlahPositif.toString()
                        vbinding.infectedNew.text =
                            this?.update?.penambahan?.jumlahSembuh.toString()
                        vbinding.inHospital.text = "Dirawat : " + this?.update?.total?.jumlahDirawat.toString()
                    }
                }
                else -> {

                }
            }
        })
         */
    }


    private fun setUpPrixaArtificalIntelligence() {
        vbinding.includeAi.webView.let {
            val webSettings = it.settings
            it.settings.domStorageEnabled = true
            webSettings.javaScriptEnabled = true
            webSettings.allowFileAccess = true
            webSettings.allowContentAccess = true
            it.clearCache(true)
            it.loadUrl(URL.PRIXA_AI)
            it.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    vbinding.includeLoading.loadingRoot.visibility = View.INVISIBLE
                    it.visibility = View.VISIBLE
                }

                override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    vbinding.includeLoading.loadingRoot.visibility = View.VISIBLE
                }

                override fun onReceivedError(
                    view: WebView,
                    errorCod: Int,
                    description: String,
                    failingUrl: String
                ) {
                    vbinding.includeLoading.loadingRoot.visibility = View.INVISIBLE
                }

            }
        }
    }

    private fun setUpViewAction() {
        
        vbinding.btnCardAddNewReport.setOnClickListener {
            startActivity(Intent(requireContext(), UserInputReportActivity::class.java))
        }

        vbinding.btnCardCheckStatusReport.setOnClickListener {
            findNavController().navigate(R.id.navigation_my_report)
        }
        vbinding.btnOpenAI.setOnClickListener {
//            vbinding.includeAi.lytPrixa.apply {
//                visibility = View.VISIBLE
//                animation = AnimationUtils.loadAnimation(
//                    requireContext(),
//                    R.anim.bottom_appear
//                )
//            }
        }

        vbinding.includeAi.btnCloseAI.setOnClickListener {
            vbinding.includeAi.lytPrixa.apply {
                visibility = View.GONE
                animation = AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.bottom_gone
                )
            }
        }
    }
}