package com.example.sherazali.idpos_app.Adapters

import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.sherazali.idpos_app.R
import com.example.sherazali.idpos_app.Retailer.Products
import kotlinx.android.synthetic.main.transactionadapter.view.*

class TransactionRecyclerAdapter(var context: Context, var products : HashMap<String, ArrayList<String>>, var barcodeHashmap: HashMap<String, Int>) : RecyclerView.Adapter<TransactionCustomViewHolder>(){


    var transactioncallback : transactionCallBack

    init {
        this.transactioncallback = context as transactionCallBack
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TransactionCustomViewHolder {
        var layoutInflater = LayoutInflater.from(parent.context)
        var layoutView = layoutInflater.inflate(R.layout.transactionadapter, parent, false)

        return TransactionCustomViewHolder(layoutView)
    }

    override fun getItemCount(): Int {

        return barcodeHashmap.count()

    }

    override fun onBindViewHolder(holder: TransactionCustomViewHolder, pos: Int) {

        var key = barcodeHashmap.keys.toList()[pos]
        var productDetails = products.getValue(key)

        try {

            //Product ID
            var productID = productDetails[2]

            //Product Description
            holder.view.transactionProductDesc_value.text = productDetails[0]

            //Product Quantity
            var quantity= barcodeHashmap.getValue(key)
            holder.view.transactionProductQuantity_value.text = quantity.toString()

            //Price calculation
            var price = productDetails[1].toInt() * quantity

            holder.view.transactionProductPrice_value.text = price.toString()



            transactioncallback.transactionDone(productID, productDetails[0], barcodeHashmap.getValue(key).toString(), price.toString())

        }catch (e: Exception){
            Log.e("Transaction Adapt Error", "$e")
        }



    }

    interface transactionCallBack{

        fun transactionDone(transactionProductID: String, transactionProductDescription:String, transactionProductQuantity:String, transactionProductPrice:String)
    }


}

class TransactionCustomViewHolder(var view: View) : RecyclerView.ViewHolder(view)