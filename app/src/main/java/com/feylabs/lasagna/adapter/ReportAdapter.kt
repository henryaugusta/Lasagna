package com.feylabs.lasagna.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.feylabs.lasagna.R
import com.feylabs.lasagna.databinding.ItemPreviewReportBinding
import com.feylabs.lasagna.data.model.api.ReportGetByUserModel
import com.feylabs.lasagna.util.networking.Endpoint

class ReportAdapter() : RecyclerView.Adapter<ReportAdapter.ReportAdapterHolder>() {

    lateinit var reportAdapterInterface: ReportAdapterInterface

    val objectList = mutableListOf<ReportGetByUserModel.Report.Data>()

    fun setData(data: MutableList<ReportGetByUserModel.Report.Data>) {
        this.objectList.clear()
        this.objectList.addAll(data)
        notifyDataSetChanged()
    }

    fun setInterface(interfaces: ReportAdapterInterface) {
        this.reportAdapterInterface = interfaces
    }

    inner class ReportAdapterHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = ItemPreviewReportBinding.bind(view)
        fun bind(model: ReportGetByUserModel.Report.Data) {
            view.labelCategory.text = model.category.category_name
            view.labelDetail.text = model.detail_alamat
            view.labelStatus.text = model.status_label
            view.labelReportedAt.text = model.created_at

            view.labelStatus.apply {
                when (model.status) {
                    "0" -> {
                        background =
                            (ContextCompat.getDrawable(context, R.drawable.ic_status_neutral))
                    }
                    "1" -> {
                        background =
                            (ContextCompat.getDrawable(context, R.drawable.ic_status_waiting))
                    }
                    "2" -> {
                        background =
                            (ContextCompat.getDrawable(context, R.drawable.ic_status_coordination))
                    }
                    "3" -> {
                        background =
                            (ContextCompat.getDrawable(context, R.drawable.ic_status_success))
                    }
                    "4" -> {
                        background =
                            (ContextCompat.getDrawable(context, R.drawable.ic_status_rejected))
                    }
                }
            }

            Glide
                .with(view.root)
                .load(Endpoint.REAL_URL + model.photo_path)
                .centerCrop()
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_loading_small_1)
                .into(view.imagePlaceholder)

            view.root.setOnClickListener {
                reportAdapterInterface.onclick(model)
            }
        }
    }


    override fun getItemCount(): Int {
        return this.objectList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportAdapterHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_preview_report, parent, false)
        return ReportAdapterHolder(view)
    }


    override fun onBindViewHolder(holder: ReportAdapterHolder, position: Int) {
        holder.bind(model = objectList[position])
    }

    interface ReportAdapterInterface {
        fun onclick(model: ReportGetByUserModel.Report.Data)
    }

}