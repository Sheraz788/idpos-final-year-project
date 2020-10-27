package com.example.sherazali.idpos_app.Adapters

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sherazali.idpos_app.R
import com.example.sherazali.idpos_app.Retailer.StoresProducts
import kotlinx.android.synthetic.main.inventory_recycleradapter.view.*

class InventoryRecyclerAdapter(var products : StoresProducts) : RecyclerView.Adapter<InventoryCustomHolder>(){

    var productsSize = products.products.count()

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): InventoryCustomHolder {


        var inflater = LayoutInflater.from(parent.context)

        var view = inflater.inflate(R.layout.inventory_recycleradapter, parent, false)

        return  InventoryCustomHolder(view)

    }

    override fun getItemCount(): Int {

        var productLength = 0

        if(productsSize == 0){
            productLength = 1
        }else{

            productLength = productsSize

        }


        return productLength

    }

    override fun onBindViewHolder(holder: InventoryCustomHolder, position: Int) {

        try {
            if (productsSize == 0) {

                holder.view.noInventoryLayout.visibility = View.VISIBLE
                holder.view.inventoryProductsLayout.visibility = View.INVISIBLE

            } else {
                holder.view.noInventoryLayout.visibility = View.INVISIBLE
                holder.view.inventoryProductsLayout.visibility = View.VISIBLE


                var storeProduct = products.products.get(position)

                holder.view.inventoryQuantity_value.text = storeProduct.Quantity.toString()
                holder.view.inventoryProductDescription_value.text = storeProduct.Product_Name
            }
        }catch (e: Exception){
            Log.e("Inventory Adapter Error", "$e")
        }


    }


}

class InventoryCustomHolder(var view: View) : RecyclerView.ViewHolder(view){

}