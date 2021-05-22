package com.feylabs.lasagna.view.ui.weather


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feylabs.lasagna.R
import com.feylabs.lasagna.databinding.ItemWeatherBinding
import com.feylabs.lasagna.data.model.api.Weather

class WeatherAdapterSimple() : RecyclerView.Adapter<WeatherAdapterSimple.WeatherAdapterHolder>() {

    companion object {
        const val SIMPLE = "SIMPLE"
        const val COMPLEX = "COMPLEX"
    }

    var type = "simple"
    lateinit var myInterface: WeatherAdapterInterface

    val objectList = mutableListOf<Weather.WeatherItem>()

    fun setData(data: MutableList<Weather.WeatherItem>) {
        this.objectList.clear()
        this.objectList.addAll(data)
        notifyDataSetChanged()
    }

    fun setInterface(interfaces: WeatherAdapterInterface) {
        this.myInterface = interfaces
    }

    inner class WeatherAdapterHolder(view: View) : RecyclerView.ViewHolder(view) {
        val vbind = ItemWeatherBinding.bind(view)

        fun bind(model: Weather.WeatherItem) {
            vbind.root.setOnClickListener {
                myInterface.onclick(model)
            }



            vbind.apply {
                this.labelTemperature.text = model.tempC + "°"
                val take8 = model.jamCuaca.takeLast(8)
                this.labelDate.text = model.jamCuaca.take(10)
                this.labelTime.text = take8.take(5)
            }

//            Glide
//                .with(vbind.root)
//                .load()
//                .skipMemoryCache(true)
//                .dontAnimate()
//                .thumbnail(Glide.with(vbind.root).load(R.raw.loading2))
//                .placeholder(R.drawable.ic_loading_small_1)
//                .into(vbind.ivCover)
        }
    }

    override fun getItemCount(): Int {
        return this.objectList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherAdapterHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_weather, parent, false)
        return WeatherAdapterHolder(view)
    }


    override fun onBindViewHolder(holder: WeatherAdapterHolder, position: Int) {
        holder.bind(objectList[position])
    }

    interface WeatherAdapterInterface {
        fun onclick(model: Weather.WeatherItem)
    }

}