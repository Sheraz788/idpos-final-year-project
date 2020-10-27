package com.example.sherazali.idpos_app

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sherazali.idpos_app.Retailer.Retailers

class RetailerRecyclerViewAdapter(val retailersData: Retailers) : RecyclerView.Adapter<retailerCustomAdapter>(){



    val retailer = listOf("Ahmad", "Raza", "Buzdaar")

    override fun getItemCount(): Int {
        return retailersData.retailers.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): retailerCustomAdapter {

        val retailerAdapterInflater = LayoutInflater.from(parent.context)
        val adapterRow = retailerAdapterInflater.inflate(R.layout.retaileradapterlayout, parent, false)

        return  retailerCustomAdapter(adapterRow)


    }


    override fun onBindViewHolder(holder: retailerCustomAdapter, position: Int) {

        var retailer = retailersData.retailers.get(position)


       // holder.view.retailerName_retails.text = retailer.RetailerName

        //Log.e("retailers count ", "${retailers.retailersList.count()}")
        //Log.e("retailers Names ", "${retailers.retailersList}")


    }

}

class retailerCustomAdapter(var view: View) : RecyclerView.ViewHolder(view){

}