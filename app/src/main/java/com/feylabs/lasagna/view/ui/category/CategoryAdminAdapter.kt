package com.feylabs.lasagna.view.ui.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.feylabs.lasagna.R
import com.feylabs.lasagna.adapter.ReportCategoryAdapter
import com.feylabs.lasagna.databinding.ItemCategoryReportBinding
import com.feylabs.lasagna.data.model.api.ReportCategoryModel
import com.feylabs.lasagna.databinding.ItemCategoryForAdminBinding
import com.feylabs.lasagna.util.networking.Endpoint.REAL_URL
import timber.log.Timber

class CategoryAdminAdapter : RecyclerView.Adapter<CategoryAdminAdapter.ReportCategoryViewHolder>() {
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


    inner class  ReportCategoryViewHolder(v: View) : RecyclerView.ViewHolder(v){
        val vbind = ItemCategoryForAdminBinding.bind(v)

        fun bind(model: ReportCategoryModel.Data, position: Int){

            vbind.root.setOnClickListener {
                reportCategoryInterface.onclick(model,vbind)
            }

            vbind.labelName.text=model.category_name
            val imgUrl = REAL_URL+model.photo_path
            Timber.d("picasso url : $imgUrl")

            Glide
                .with(vbind.root)
                .load(imgUrl)
                .skipMemoryCache(true)
                .dontAnimate()
                .thumbnail(Glide.with(vbind.root).load(R.raw.loading2))
                .placeholder(R.drawable.ic_loading_small_1)
                .into(vbind.ivCover)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportCategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category_for_admin,parent,false)
        return ReportCategoryViewHolder(view)
    }


    override fun getItemCount(): Int {
        return myData.size
    }

    interface MyCategoryInterface{
        fun onclick(model: ReportCategoryModel.Data, adaptVbind : ItemCategoryForAdminBinding)
    }

    override fun onBindViewHolder(
        holder: ReportCategoryViewHolder,
        position: Int
    ) {
        holder.bind(myData[position],position)
    }

}