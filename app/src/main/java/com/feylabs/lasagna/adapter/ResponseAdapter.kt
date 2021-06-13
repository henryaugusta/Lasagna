package com.feylabs.lasagna.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.feylabs.lasagna.R
import com.feylabs.lasagna.data.model.ReportDetailModel
import com.feylabs.lasagna.databinding.ItemReportResponseBinding
import com.feylabs.lasagna.util.networking.Endpoint.REAL_URL

class ResponseAdapter() : RecyclerView.Adapter<ResponseAdapter.ResponseHolder>() {

    lateinit var myInterface: ResponseInterface

    val dataList = mutableListOf<ReportDetailModel.Report.Response>()

    fun setData(data: MutableList<ReportDetailModel.Report.Response>) {
        this.dataList.clear()
        this.dataList.addAll(data)
        notifyDataSetChanged()
    }

    fun setInterface(interfaceN: ResponseInterface) {
        this.myInterface = interfaceN
    }

    inner class ResponseHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = ItemReportResponseBinding.bind(view)
        fun bind(model: ReportDetailModel.Report.Response) {
            view.labelResponder.text = model.responder
            Glide
                .with(view.root)
                .load(REAL_URL + model.path)
                .centerCrop()
                .skipMemoryCache(true)
                .placeholder(R.drawable.ic_loading_small_1)
                .into(view.imgRes)

            view.labelTanggal.text = model.createdAt
            view.labelStatus.text=model.statusLabel
            view.labelStatus.apply {
                when (model.statusCode.toString()) {
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

            view.root.setOnClickListener {
                myInterface.onclick(model)
            }
        }

    }


    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponseHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_response, parent, false)
        return ResponseHolder(view)
    }

    interface ResponseInterface {
        fun onclick(model: ReportDetailModel.Report.Response)
    }

    override fun onBindViewHolder(holder: ResponseHolder, position: Int) {
        holder.bind(model = dataList[position])
    }
}