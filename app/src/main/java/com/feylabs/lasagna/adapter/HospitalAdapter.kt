package com.feylabs.lasagna.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.feylabs.lasagna.R
import com.feylabs.lasagna.databinding.ItemHospitalBinding
import com.feylabs.lasagna.data.model.api.HospitalModel

class HospitalAdapter() : RecyclerView.Adapter<HospitalAdapter.HospitalAdapterHolder>() {

    lateinit var hospitalAdapterInterface: HospitalAdapterInterface

    val objectList = mutableListOf<HospitalModel.Data>()

    fun setData(data: MutableList<HospitalModel.Data>) {
        this.objectList.clear()
        this.objectList.addAll(data)
        notifyDataSetChanged()
    }

    fun setInterface(interfaces: HospitalAdapterInterface) {
        this.hospitalAdapterInterface = interfaces
    }

    inner class HospitalAdapterHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vbind = ItemHospitalBinding.bind(view)
        fun bind(hospitalModel: HospitalModel.Data) {
            vbind.root.setOnClickListener {
                hospitalAdapterInterface.onclick(hospitalModel)
            }

            vbind.apply {
                labelName.text=hospitalModel.name
                labelAddress.text = hospitalModel.alamat
                labelFacility.text = hospitalModel.fasilitas
                labelOfficeHour.text = hospitalModel.operasional
            }

            Glide
                .with(vbind.root)
                .load(hospitalModel.real_photo_path)
                .skipMemoryCache(true)
                .dontAnimate()
                .thumbnail(Glide.with(vbind.root).load(R.raw.loading2))
                .placeholder(R.drawable.ic_loading_small_1)
                .into(vbind.ivCover)
        }
    }

    override fun getItemCount(): Int {
        return this.objectList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalAdapterHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hospital, parent, false)
        return HospitalAdapterHolder(view)
    }


    override fun onBindViewHolder(holder: HospitalAdapterHolder, position: Int) {
        holder.bind(objectList[position])
    }

    interface HospitalAdapterInterface {
        fun onclick(model: HospitalModel.Data)
    }

}