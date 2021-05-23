package com.feylabs.lasagna.view.ui.daily_covid


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.feylabs.lasagna.R
import com.feylabs.lasagna.data.model.api.GovCovidData
import com.feylabs.lasagna.databinding.ItemWeatherBinding
import com.feylabs.lasagna.data.model.api.Weather
import com.feylabs.lasagna.databinding.ItemCovidDetailBinding

class CovidAdapter() : RecyclerView.Adapter<CovidAdapter.CovidAdapterHolder>() {

    companion object {
        const val SIMPLE = "SIMPLE"
        const val COMPLEX = "COMPLEX"
    }

    var type = "simple"

    val objectList = mutableListOf<GovCovidData.Update.Harian>()

    fun setData(data: MutableList<GovCovidData.Update.Harian>) {
        this.objectList.clear()
        this.objectList.addAll(data)
        notifyDataSetChanged()
    }


    inner class CovidAdapterHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vbind = ItemCovidDetailBinding.bind(view)

        fun bind(model: GovCovidData.Update.Harian) {

            vbind.apply {
                labelDate.text = model.keyAsString.take(10)
                labelDirawat.text = model.jumlahDirawat.value.toString()
                labelTotalDirawat.text = model.jumlahDirawatKum.value.toString()

                labelMeninggal.text = model.jumlahMeninggal.value.toString()
                labelTotalMeninggal.text = model.jumlahMeninggalKum.value.toString()

                labelTotalDirawat.text = model.jumlahDirawatKum.value.toString()
                labelDirawat.text = model.jumlahDirawat.value.toString()

                labelPositif.text = model.jumlahPositif.value.toString()
                labelTotalPositif.text = model.jumlahPositifKum.value.toString()

                labelSembuh.text = model.jumlahSembuh.value.toString()
                labelTotalSembuh.text = model.jumlahSembuhKum.value.toString()
            }


        }
    }

    override fun getItemCount(): Int {
        return this.objectList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CovidAdapterHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_covid_detail, parent, false)
        return CovidAdapterHolder(view)
    }


    override fun onBindViewHolder(holder: CovidAdapterHolder, position: Int) {
        holder.bind(objectList[position])
    }


}