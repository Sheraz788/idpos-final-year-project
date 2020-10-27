package com.example.sherazali.idpos_app.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sherazali.idpos_app.R
import com.example.sherazali.idpos_app.Retailer.StoresTrends
import kotlinx.android.synthetic.main.trends_adapter.view.*

class TrendsAdapter(var storesTrends: StoresTrends) : RecyclerView.Adapter<TrendsCustomHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): TrendsCustomHolder {

        var layoutInflater = LayoutInflater.from(parent.context)
        var trendsLayout = layoutInflater.inflate(R.layout.trends_adapter, parent, false)
        return TrendsCustomHolder(trendsLayout)
    }

    override fun getItemCount(): Int {

        return storesTrends.trends.count()

    }

    override fun onBindViewHolder(holder: TrendsCustomHolder, pos: Int) {


        var storetrend = storesTrends.trends.get(pos)


        holder.view.trendsProductName_txt.text = storetrend.ProductName
        holder.view.trendsProductCategory_txt.text = storetrend.CategoryName
        holder.view.trendsProductSaleCount_txt.text = storetrend.ProductCount.toString()


    }

}

class TrendsCustomHolder(var view:View) : RecyclerView.ViewHolder(view)