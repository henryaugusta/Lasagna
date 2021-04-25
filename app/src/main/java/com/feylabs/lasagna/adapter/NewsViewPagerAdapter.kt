package com.feylabs.lasagna.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.feylabs.lasagna.model.ModelNewsCarousel
import com.feylabs.lasagna.view.ui.news.NewsCarouselFragment

class NewsViewPagerAdapter(fm:FragmentManager , lf : Lifecycle) : FragmentStateAdapter(fm,lf) {

    val listNews = mutableListOf<ModelNewsCarousel>()

    override fun getItemCount(): Int {
       return  listNews.size
    }

    override fun createFragment(position: Int): Fragment {
        return NewsCarouselFragment()
    }
}