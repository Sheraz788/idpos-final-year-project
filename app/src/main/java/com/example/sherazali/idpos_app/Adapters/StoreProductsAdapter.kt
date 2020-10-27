package com.example.sherazali.idpos_app.Adapters

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sherazali.idpos_app.Customer.CartActivity
import com.example.sherazali.idpos_app.R
import com.example.sherazali.idpos_app.Retailer.Categories
import com.example.sherazali.idpos_app.Retailer.Category
import kotlinx.android.synthetic.main.content_customer_store_home.view.*
import kotlinx.android.synthetic.main.product_row.view.*
import java.lang.Exception

class StoreProductsAdapter(var storeproducts: Categories) : RecyclerView.Adapter<StoreProductHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): StoreProductHolder {

        var productRowLayout = LayoutInflater.from(parent?.context).inflate(R.layout.product_row,parent,false)

        return StoreProductHolder(productRowLayout)

    }

    override fun getItemCount(): Int {

        return storeproducts.categories.count()

    }

    override fun onBindViewHolder(holder: StoreProductHolder, pos: Int) {
        var product = storeproducts.categories.get(pos)


        holder.view.storeProductName_value.text = product.Product_Name
        holder.view.storeProductPrice_value.text = product.Price.toString()


        holder.product = product

    }






}

class StoreProductHolder(var view: View, var product: Category? = null) : RecyclerView.ViewHolder(view){

    var cartItemsCount = 0
    companion object {
        var PRODUCT_KEY = "PRODUCT_KEY"
    }

    lateinit var cartinterface : cartInterface

    init {

        try {
            this.cartinterface = (view.context as cartInterface)

            view.setOnClickListener {


                try {
                    cartinterface.cartItems(product!!.ProductID, product!!.Product_Name, product!!.Quantity, product!!.Price)
                }catch (e: Exception){
                    Log.e("Cart Count", "$e")
                }

            }
        }catch (e: Exception){
            Log.e("Adding Cart Items Error", "$e")
        }
    }


    interface cartInterface{

        fun cartItems(productID:Int, productName: String, productQuantity: Int, productPrice:Int)
    }

}