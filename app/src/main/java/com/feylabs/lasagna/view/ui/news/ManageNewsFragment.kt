package com.feylabs.lasagna.view.ui.news

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.feylabs.lasagna.R
import com.feylabs.lasagna.data.model.ModelNewsCarousel
import com.feylabs.lasagna.databinding.FragmentAddNewsBinding
import com.feylabs.lasagna.databinding.FragmentManageNewsBinding
import com.feylabs.lasagna.util.Resource
import com.feylabs.lasagna.util.SharedPreference.Preference
import com.feylabs.lasagna.util.SharedPreference.const.USER_TYPE
import com.feylabs.lasagna.util.baseclass.BaseFragment
import com.feylabs.lasagna.view.bottom_sheet.NewsBottomSheet
import com.feylabs.lasagna.viewmodel.NewsViewModel
import com.google.android.material.button.MaterialButton
import com.squareup.picasso.Picasso
import timber.log.Timber


class ManageNewsFragment : BaseFragment() {


    val newsViewModel by lazy { ViewModelProvider(requireActivity()).get(NewsViewModel::class.java) }
    val newsAdapter by lazy { NewsAdapterAdmin() }

    lateinit var binding: FragmentManageNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_manage_news, container, false)
        binding = FragmentManageNewsBinding.bind(view)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        newsViewModel.fetchNews()

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_news_to_addNewsFragment)
        }


        setupRecyclerView()
        setUpAdapter()
        setupObserver()

        binding.srl.setOnRefreshListener {
            binding.srl.isRefreshing = false
            newsViewModel.fetchNews()
        }

    }

    private fun setUpAdapter() {
        //Set News Item On Click
        newsAdapter.setInterface(object : NewsAdapterAdmin.NewsAdapterInterface {
            override fun onclick(model: ModelNewsCarousel) {
                model.title.showLongToast()
                val motDetailBottomSheet = NewsBottomSheet(requireActivity())
                motDetailBottomSheet.apply {
                    val closeBtn = findViewById<ImageButton>(R.id.btn_close_detail_news)
                    val deleteBtn = findViewById<MaterialButton>(R.id.btn_delete)
                    val content = findViewById<TextView>(R.id.tv_detail_news_content)
                    val title = findViewById<TextView>(R.id.tv_det_news_title)
                    val author = findViewById<TextView>(R.id.tv_detail_news_author)
                    val image = findViewById<ImageView>(R.id.iv_det_news_img)
                    title.text = model.title
                    author.text = model.author

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        content.text = (Html.fromHtml(model.content, Html.FROM_HTML_MODE_COMPACT))
                    } else {
                        content.text = (Html.fromHtml(model.content))
                    }

                    if (Preference(requireContext()).getPrefString(USER_TYPE) != "admin") {
                        deleteBtn.visibility = View.GONE
                    }

                    deleteBtn.setOnClickListener {

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
        binding.rv.apply {
            setHasFixedSize(true)
            adapter = newsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupObserver() {
        newsViewModel.newsDB.observe(viewLifecycleOwner, Observer {
            when (it) {
                is Resource.Loading -> {
                    Timber.d("newsz: loading")
                    binding?.includeLoading?.loadingRoot?.visibility = View.VISIBLE
                }
                is Resource.Error -> {
                    binding?.includeLoading?.loadingRoot?.visibility = View.GONE
                    Timber.d("newsz: error")
                    "Gagal Terhubung Dengan Server".showLongToast()
                }
                is Resource.Success -> {
                    "Success".showLongToast()
                    it.data?.let { it1 ->
                        newsAdapter.setData(it1)
                        Timber.d("newsz: success")
                        Timber.d("newsz: ${it1.toString()}")
                    }
                    newsAdapter.notifyDataSetChanged()
                    binding?.includeLoading?.loadingRoot?.visibility = View.GONE

                }
            }
        })

    }

}