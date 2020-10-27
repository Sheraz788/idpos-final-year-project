package com.example.sherazali.idpos_app.Adapters

import android.app.Dialog
import android.content.Context
import android.preference.PreferenceManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.R
import com.example.sherazali.idpos_app.Retailer.Product
import com.example.sherazali.idpos_app.Retailer.Products
import com.example.sherazali.idpos_app.Retailer.ProductsActivity
import kotlinx.android.synthetic.main.activity_products.*
import kotlinx.android.synthetic.main.activity_products.view.*
import kotlinx.android.synthetic.main.productrecycleradapter.view.*
import java.lang.Exception

class ProductRecyclerAdapter(val products: Products, var context: Context) : RecyclerView.Adapter<ProductsCustomHolder>() {

    var selectedProductList = ArrayList<Int>()

    var productIsSelected = false

    var adapterCallBack : AdapterCallBack

    lateinit var productDialog : Dialog

    init{
        try
        {
            this.adapterCallBack = (context as AdapterCallBack)
        }
        catch (e:ClassCastException) {
            throw ClassCastException("Activity must implement AdapterCallback.")
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ProductsCustomHolder {

        var productLayout = LayoutInflater.from(parent.context)
        var productRow = productLayout.inflate(R.layout.productrecycleradapter, parent, false)

         return ProductsCustomHolder(productRow)

    }

    override fun getItemCount(): Int {

        return products.products.count()

    }

    override fun onBindViewHolder(holder: ProductsCustomHolder, position: Int) {

        var product = products.products.get(position)


        var productActivity = ProductsActivity()


        productDialog = Dialog(context)



        holder.view.storeProductValue.text = product.Description

        var productMap = HashMap<Int, String>()

        productMap.put(product.ProductID, product.Description)



        holder.view.product_CheckBox.setOnCheckedChangeListener { buttonView, Checked ->




            if (Checked) {


                adapterCallBack.onMethodCallBack(true, product.ProductID, holder)


                PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("Checked", Checked).commit()

                for (productDetail in productMap) {

                    selectedProductList.add(productDetail.key)

                }

                Log.e("Checked", "$Checked")

            } else {


                adapterCallBack.onMethodCallBack(Checked, product.ProductID,holder)




            }


        }



    }



    interface AdapterCallBack{
        fun onMethodCallBack(value: Boolean, productID: Int, holder: ProductsCustomHolder)
    }


}

class ProductsCustomHolder(var view: View)  : RecyclerView.ViewHolder(view){

    var productCheckBox : CheckBox = view.findViewById(R.id.product_CheckBox)

}