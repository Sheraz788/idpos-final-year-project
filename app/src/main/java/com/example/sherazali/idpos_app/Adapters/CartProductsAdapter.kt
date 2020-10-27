package com.example.sherazali.idpos_app.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sherazali.idpos_app.R
import kotlinx.android.synthetic.main.cart_row.view.*

class CartProductsAdapter(var cartProductsList: HashMap<Int,ArrayList<String>>, var context: Context) : RecyclerView.Adapter<CartCustomHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, pos: Int): CartCustomHolder {

        var cartLayout = LayoutInflater.from(parent.context)
        var layoutView = cartLayout.inflate(R.layout.cart_row, parent, false)

        return CartCustomHolder(layoutView)
    }

    override fun getItemCount(): Int {

        return cartProductsList.size

    }

    override fun onBindViewHolder(holder: CartCustomHolder, pos: Int) {

        var key = cartProductsList.keys.toList()[pos]
        var productDetails = cartProductsList.getValue(key)

        try{
            holder.view.productAddedToCart_value.text = productDetails[0]
            holder.view.cartProductPrice_value.text = productDetails[1]
            holder.view.cartProductQuantity_value.text = productDetails[2]


        }catch (e:Exception){
            Log.e("Cart Products Error","$e")
        }

    }


}

class CartCustomHolder(var view: View) : RecyclerView.ViewHolder(view){

}