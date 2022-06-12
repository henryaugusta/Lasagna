package com.bangkit.nadira.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bangkit.nadira.R
import com.bangkit.nadira.data.model.api.ReportCategoryModel
import com.bangkit.nadira.databinding.ItemCategoryReportNewBinding
import com.bangkit.nadira.util.networking.Endpoint.REAL_URL
import timber.log.Timber

class ReportCategoryAdapter : RecyclerView.Adapter<ReportCategoryAdapter.ReportCategoyViewHolder>() {
    val myData = mutableListOf<ReportCategoryModel.Data>()

    lateinit var reportCategoryInterface: MyCategoryInterface
    fun setData(newData : MutableList<ReportCategoryModel.Data>){
        myData.clear()
        myData.addAll(newData)
        notifyDataSetChanged()
    }

    fun setInterface(newInterface : MyCategoryInterface){
        this.reportCategoryInterface = newInterface
    }



    inner class  ReportCategoyViewHolder(v: View) : RecyclerView.ViewHolder(v){
        val vbind = ItemCategoryReportNewBinding.bind(v)


        fun bind(model: ReportCategoryModel.Data, position: Int){

            vbind.root.setOnClickListener {
                reportCategoryInterface.onclick(model,vbind)
            }

            vbind.textView.text=model.category_name
            val imgUrl = REAL_URL+model.photo_path
            Timber.d("picasso url : $imgUrl")

            Glide
                .with(vbind.root)
                .load(imgUrl)
                .skipMemoryCache(true)
                .dontAnimate()
                .thumbnail(Glide.with(vbind.root).load(R.raw.loading2))
                .placeholder(R.drawable.ic_loading_small_1)
                .into(vbind.imagePlaceholder)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportCategoyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_report_new,parent,false)
        return ReportCategoyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportCategoyViewHolder, position: Int) {
        holder.bind(myData[position],position)
    }

    override fun getItemCount(): Int {
        return myData.size
    }

    interface MyCategoryInterface{
        fun onclick(model: ReportCategoryModel.Data, adaptVbind: ItemCategoryReportNewBinding)
    }

}