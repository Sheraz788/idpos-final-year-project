package com.example.sherazali.idpos_app.Customer
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.example.sherazali.idpos_app.Adapters.CartProductsAdapter
import com.example.sherazali.idpos_app.R
import kotlinx.android.synthetic.main.cart_activity.*



class CartActivity : AppCompatActivity() {

    var products:MutableList<String> = ArrayList()
    var displayList:MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart_activity)


        // Get bundle data from intent
        val data = intent.extras

// Create a new map
        val cartItemsHashMap = HashMap<Int, ArrayList<String>>()

// Get hash map size
        val size = data!!.getInt("size")

// Get each entry from bundle then put to hash map
        for (i in 0 until size) {
            val key = data.getInt("key$i")
            val value = data.getStringArrayList("value$i")
            cartItemsHashMap.put(key, value)
        }
        Log.e("product detail", "$cartItemsHashMap")

        cartProducts_recyclerView.layoutManager = LinearLayoutManager(this)
        cartProducts_recyclerView.adapter = CartProductsAdapter(cartItemsHashMap, this)



        checkout_Btn.setOnClickListener {

            var checkOutIntent = Intent(this, CheckoutActivity::class.java)

            val cartItemsData = Bundle()

            // Save hash map size
            cartItemsData.putInt("size", cartItemsHashMap.size)

            // Save each entry (String, String)
            var i = 0
            for (entry in cartItemsHashMap.entries) {
                val key = entry.key
                val value = entry.value
                cartItemsData.putInt("key$i", key)
                cartItemsData.putStringArrayList("value$i", value)
                ++i
            }

            checkOutIntent.putExtras(cartItemsData)
            startActivity(checkOutIntent)

        }

        back_arrow.setOnClickListener {

            var homeIntent = Intent(this, CustomerStoreHomeActivity::class.java)
            startActivity(homeIntent)
            overridePendingTransition(

                R.anim.trans_left_in,
                R.anim.trans_right_out
            )
            finish()

        }


    }

    override fun onBackPressed() {

        var homeIntent = Intent(this, CustomerStoreHomeActivity::class.java)
        startActivity(homeIntent)
        overridePendingTransition(

            R.anim.trans_left_in,
            R.anim.trans_right_out
        )
        finish()

        super.onBackPressed()
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {



         menuInflater.inflate(R.menu.search,menu)
        val searchItem = menu.findItem(R.id.menu_search)
        if(searchItem != null){
            val searchView = searchItem.actionView as SearchView
            val editext = searchView.findViewById<EditText>(android.support.v7.appcompat.R.id.search_src_text)
            editext.hint = "Search Products..."

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {

                    displayList.clear()
                    if(newText!!.isNotEmpty()){
                        val search = newText.toLowerCase()
                        products.forEach {
                            if(it.toLowerCase().contains(search)){
                                displayList.add(it)
                            }
                        }
                    }else{
                        displayList.addAll(products)
                    }
                    cartProducts_recyclerView.adapter?.notifyDataSetChanged()
                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu)
    }



}
