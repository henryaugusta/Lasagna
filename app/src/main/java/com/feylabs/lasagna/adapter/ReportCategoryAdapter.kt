package com.feylabs.lasagna.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feylabs.lasagna.R
import com.feylabs.lasagna.databinding.ItemCategoryReportBinding
import com.feylabs.lasagna.model.api.ReportCategoryModel
import com.feylabs.lasagna.util.networking.Endpoint.REAL_URL
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
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
        val vbind = ItemCategoryReportBinding.bind(v)


        fun bind(model: ReportCategoryModel.Data, position: Int){

            vbind.root.setOnClickListener {
                reportCategoryInterface.onclick(model,vbind)
            }

            vbind.textView.text=model.category_name
            val imgUrl = REAL_URL+model.photo_path
            Timber.d("picasso url : $imgUrl")
            Picasso.get()
                .load(imgUrl)
                .placeholder(R.drawable.ic_loading_gif)
                .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
                .into(vbind.imagePlaceholder)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportCategoyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_report,parent,false)
        return ReportCategoyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportCategoyViewHolder, position: Int) {
        holder.bind(myData[position],position)
    }

    override fun getItemCount(): Int {
        return myData.size
    }

    interface MyCategoryInterface{
        fun onclick(model: ReportCategoryModel.Data, adaptVbind : ItemCategoryReportBinding)
    }

}