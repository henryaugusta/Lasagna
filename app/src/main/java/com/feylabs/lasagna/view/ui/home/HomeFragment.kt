package com.feylabs.lasagna.view.ui.home

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.feylabs.lasagna.R
import com.feylabs.lasagna.adapter.NewsAdapterCarousel
import com.feylabs.lasagna.adapter.NewsViewPagerAdapter
import com.feylabs.lasagna.databinding.FragmentHomeBinding
import com.feylabs.lasagna.databinding.LayoutDetailNewsBinding
import com.feylabs.lasagna.model.ModelNewsCarousel
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.URL
import com.feylabs.lasagna.util.baseclass.BaseFragment
import com.feylabs.lasagna.util.baseclass.Util
import com.feylabs.lasagna.view.MainMenuUserViewModel
import com.feylabs.lasagna.viewmodel.NewsViewModel
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.NonCancellable.isActive
import timber.log.Timber


class HomeFragment : BaseFragment() {

    private lateinit var homeViewModel: HomeViewModel

    val newsViewModel by lazy { ViewModelProvider(requireActivity()).get(NewsViewModel::class.java) }
    val menuViewModel by lazy { ViewModelProvider(requireActivity()).get(MainMenuUserViewModel::class.java) }


    lateinit var vbinding: FragmentHomeBinding
    lateinit var newsBottomSheetBinding: LayoutDetailNewsBinding

    lateinit var newsAdapterCarousel: NewsAdapterCarousel

    override fun onResume() {
        super.onResume()
        if (newsViewModel.newsDB.value==null){
            newsViewModel.fetchNews()
        }
    }

    override fun onStart() {
        super.onStart()
    }


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

        menuViewModel.title.value="Beranda"

        newsAdapterCarousel = NewsAdapterCarousel()


        vbinding.containerCardNews.apply {
            setHasFixedSize(true)
            adapter = newsAdapterCarousel
            layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        }

        vbinding.indicator.attachToRecyclerView(vbinding.containerCardNews)




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

        vbinding.containerCardNews.adapter = newsAdapterCarousel

        setUpViewAction()
        setUpPrixaArtificalIntelligence()

        newsViewModel.newsDB.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    Timber.d("register: loading")
                    vbinding.includeLoading.loadingRoot.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    vbinding.includeLoading.loadingRoot.visibility = View.GONE
                    Timber.d("register: error")
                    "Error".showLongToast()
                }
                is Resource.Success -> {
                    it.data?.let { it1 -> newsAdapterCarousel.setData(it1) }
                    newsAdapterCarousel.notifyDataSetChanged()
                    vbinding.includeLoading.loadingRoot.visibility = View.GONE
                    "Success Mengambil Data Berita ${it.data?.size}".showLongToast()
                    Timber.d("register: success")
                }

            }
        })
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
        vbinding.btnOpenAI.setOnClickListener {
            vbinding.includeAi.lytPrixa.apply {
                visibility = View.VISIBLE
                animation = AnimationUtils.loadAnimation(
                    requireContext(),
                    R.anim.bottom_appear
                )
            }
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