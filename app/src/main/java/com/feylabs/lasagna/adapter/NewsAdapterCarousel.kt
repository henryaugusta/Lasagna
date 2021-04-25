package com.feylabs.lasagna.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feylabs.lasagna.R
import com.feylabs.lasagna.databinding.ItemNewsBinding
import com.feylabs.lasagna.model.ModelNewsCarousel
import com.github.islamkhsh.CardSliderAdapter
import com.squareup.picasso.Picasso

class NewsAdapterCarousel() : RecyclerView.Adapter<NewsAdapterCarousel.NewsAdapterCarouselHolder>() {

    lateinit var newsAdapterInterface: NewsAdapterInterface

    val newsList = mutableListOf<ModelNewsCarousel>()

    fun setData(data: MutableList<ModelNewsCarousel>) {
        this.newsList.clear()
        this.newsList.addAll(data)
        notifyDataSetChanged()
    }

    fun setInterface(interfaceNews: NewsAdapterInterface) {
        this.newsAdapterInterface = interfaceNews
    }

    inner class NewsAdapterCarouselHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = ItemNewsBinding.bind(view)
        fun bind(model: ModelNewsCarousel) {
            view.labelNewsTitle.text = model.title
            Picasso.get()
                .load(model.photo_img)
                .into(view.imagePlaceholder)

            view.root.setOnClickListener {
                newsAdapterInterface.onclick(model)
            }
        }

    }


    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapterCarouselHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsAdapterCarouselHolder(view)
    }

    interface NewsAdapterInterface {
        fun onclick(model : ModelNewsCarousel)
    }

    override fun onBindViewHolder(holder: NewsAdapterCarouselHolder, position: Int) {
        holder.bind(model = newsList[position])
    }
}