package com.bangkit.nadira.view.ui.nfc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.nadira.R
import com.bangkit.nadira.data.model.api.MyNfcListResponse
import com.bangkit.nadira.databinding.ItemMyNfcListBinding
class MyNFCAdapter() : RecyclerView.Adapter<MyNFCAdapter.ReportAdapterHolder>() {

    lateinit var reportAdapterInterface: MyNfcListAdapterInterface

    val objectList = mutableListOf<MyNfcListResponse.MyNfcListResponseItem>()

    fun setData(data: MutableList<MyNfcListResponse.MyNfcListResponseItem>) {
        this.objectList.clear()
        this.objectList.addAll(data)
        notifyDataSetChanged()
    }

    fun setInterface(interfaces: MyNfcListAdapterInterface) {
        this.reportAdapterInterface = interfaces
    }

    inner class ReportAdapterHolder(view: View) : RecyclerView.ViewHolder(view) {
        val view = ItemMyNfcListBinding.bind(view)
        fun bind(model: MyNfcListResponse.MyNfcListResponseItem) {
            view.labelName.text=model.label
            view.labelCardId.text=model.cardId
            view.labelCreatedAt.text=model.createdAt
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
            .inflate(R.layout.item_my_nfc_list, parent, false)
        return ReportAdapterHolder(view)
    }


    override fun onBindViewHolder(holder: ReportAdapterHolder, position: Int) {
        holder.bind(model = objectList[position])
    }

    interface MyNfcListAdapterInterface {
        fun onclick(model: MyNfcListResponse.MyNfcListResponseItem)
    }

}
