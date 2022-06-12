package com.bangkit.nadira.view.ui.news

import android.os.Build
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bangkit.nadira.R
import com.bangkit.nadira.data.model.ModelNewsCarousel
import com.bangkit.nadira.databinding.ItemNewsListBinding

class NewsAdapterAdmin() : RecyclerView.Adapter<NewsAdapterAdmin.NewsAdapterCarouselHolder>() {

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
        val view = ItemNewsListBinding.bind(view)
        fun bind(model: ModelNewsCarousel) {
            view.labelName.text = model.title
            view.labelContent.text = model.content
            view.labelAuthor.text = model.author

            Glide
                .with(view.root)
                .load(model.photo_img)
                .skipMemoryCache(true)
                .dontAnimate()
                .thumbnail(Glide.with(view.root).load(R.raw.loading2))
                .placeholder(R.drawable.ic_loading_small_1)
                .into(view.ivCover)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.labelContent.text = (Html.fromHtml(model.content, Html.FROM_HTML_MODE_COMPACT))
            } else {
                view.labelContent.text = (Html.fromHtml(model.content))
            }

            Log.d("imgz",model.photo_img)

            view.root.setOnClickListener {
                newsAdapterInterface.onclick(model)
            }
        }

    }


    override fun getItemCount(): Int {
        return newsList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapterCarouselHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news_list, parent, false)
        return NewsAdapterCarouselHolder(view)
    }

    interface NewsAdapterInterface {
        fun onclick(model : ModelNewsCarousel)
    }

    override fun onBindViewHolder(holder: NewsAdapterCarouselHolder, position: Int) {
        holder.bind(model = newsList[position])
    }
}