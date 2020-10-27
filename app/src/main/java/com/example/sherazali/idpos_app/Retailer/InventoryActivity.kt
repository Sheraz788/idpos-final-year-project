package com.example.sherazali.idpos_app.Retailer

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.sherazali.idpos_app.Adapters.InventoryRecyclerAdapter
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.Home.HomeActivity
import com.example.sherazali.idpos_app.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_inventory.*
import okhttp3.*
import java.io.IOException

class InventoryActivity : AppCompatActivity() {

    var ipAddress = ""
    var UserID : String? = null
    var URL = ""
    var storeID : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory)



        var globals = Globals

        ipAddress = globals.ipAddress

        UserID = globals.userID
        inventory_recyclerView.layoutManager = LinearLayoutManager(this)

        back_arrow.setOnClickListener {
            val home_intent = Intent(this, HomeActivity::class.java)
            startActivity(home_intent)
            overridePendingTransition(
                R.anim.trans_left_in,
                R.anim.trans_right_out
            )
            finish()
        }

        fetchRetailersData()
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val home_intent = Intent(this, HomeActivity::class.java)
        startActivity(home_intent)
        overridePendingTransition(
            R.anim.trans_left_in,
            R.anim.trans_right_out
        )
        finish()

    }

    fun fetchRetailersData(){

        try {

            if(Globals.userType == "R"){
                URL = "http://$ipAddress:8000/retailer/$UserID"
            }else {
                URL = "http://$ipAddress:8000/saleman_retailer/$UserID"
            }

            val client = OkHttpClient()


            val request = Request.Builder().url(URL).build()


            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {


                }

                override fun onResponse(call: Call, response: Response) {


                    var body = response.body()?.string()

                    var gson = GsonBuilder().create()

                    var retailers = gson.fromJson(body, ProductRetailers::class.java)

//                    Log.e("Retailers", "$body")


                    runOnUiThread {

                        //retailersRcyclerView.adapter = RetailerRecyclerViewAdapter(retailers)

                        //Setting a key value pair hash map for retailer name and retailer ID
                        var retailerMap = HashMap<Int, String>()



                        for(retailer in retailers.retailers){

                            //retailerMap.put(1,"Select a Retailer!")
                            retailerMap.put(retailer.RetailerID, retailer.RetailerName)


                        }



                        var retailersList : List<String> = ArrayList()

                        for(retailerEntry in retailerMap){


                            retailersList += retailerEntry.value

                        }
//                        Log.e("retailers List", "$retailersList")

                        var retailerArrayAdapter = ArrayAdapter<String>(this@InventoryActivity, android.R.layout.simple_spinner_item, retailersList)
                        retailerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


                        selectRetailerInventory_spinner.adapter = retailerArrayAdapter

                        selectRetailerInventory_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


//                                if(parent?.getItemAtPosition(position)!!.equals("Select a Retailer!")){
//
//                                }else{
//
//                                }


                                fetchStores()

                            }

                        }




                    }



                }


            })
        }catch (e: Exception){
            Log.e("Retailer Exception", "$e")
        }


    }

    fun fetchStores(){

        var URL = ""

        if(Globals.userType == "R"){
            URL = "http://$ipAddress:8000/fetch_stores/$UserID"

            Log.e("URL","$URL")
        }else{
            URL = "http://$ipAddress:8000/saleman_stores/$UserID"
        }

        var client = OkHttpClient()


//        retailerID = retailerIDs


        var request = Request.Builder().url(URL).build()


        client.newCall(request).enqueue(object : Callback{

            override fun onFailure(call: Call, e: IOException) {


            }

            override fun onResponse(call: Call, response: Response) {

                var body = response.body()?.string()


                var gson = GsonBuilder().create()


                var stores = gson.fromJson(body, Stores::class.java)


                Log.e("Stores", "$stores")

                runOnUiThread {


                    var storesMap = HashMap<Int, String>()
                    var storesList = ArrayList<String>()

                    if(stores.stores.isEmpty()){

                        storesMap.put(1,"No store for this retailer!")


                    }else{

                        for(store in stores.stores){

                            //storesList.add(store.StoreName)
                            storesMap.put(store.StoreID, store.StoreName)

                        }
                    }


                    for (storeNames in storesMap){

                        storesList.add(storeNames.value)

                    }



                    var storeArrayAdapter = ArrayAdapter<String>(this@InventoryActivity, android.R.layout.simple_spinner_dropdown_item, storesList)


                    selectStoreInventory_spinner.adapter = storeArrayAdapter


                    selectStoreInventory_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {


                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {



                            if(storesMap.values.toList()[position].equals("No store for this retailer!")){
                                Toast.makeText(this@InventoryActivity, "Add Store then products!", Toast.LENGTH_SHORT).show()



                                fetchCategoriesData()
                            }else
                            {

                                storeID = storesMap.keys.toList()[position]

                                fetchCategoriesData()
                            }




                        }


                    }


                }



            }

        })




    }

    fun fetchCategoriesData(){


        var URL = "http://$ipAddress:8000/storesproducts/$storeID"

        var client = OkHttpClient()

        var request = Request.Builder().url(URL).build()


        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {

                    Toast.makeText(this@InventoryActivity, "Error in Fetching Categories $e", Toast.LENGTH_LONG).show()

                }


            }

            override fun onResponse(call: Call, response: Response) {

                var body = response.body()?.string()



                var gson = GsonBuilder().create()

                var categories = gson.fromJson(body, Categories::class.java)


//                Log.e("Categories added Store", "${categories.categories}")
                runOnUiThread {


                    var categoriesMap = HashMap<Int, String>()


                    if(categories.categories.isEmpty()){

                        categoriesMap.put(1, "No category exists in this store!")

                    }else{

                        for (category in categories.categories){


                            categoriesMap.put(category.CategoryID, category.Description)


                        }

                    }



                    var categoriesList : List<String> = ArrayList()


                    for(category in categoriesMap){


                        categoriesList += category.value


                    }


                    var categoriesArrayAdapter = ArrayAdapter<String>(this@InventoryActivity, android.R.layout.simple_spinner_item, categoriesList)

                    categoriesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    inventoryProductCategories_spinner.adapter = categoriesArrayAdapter


                    inventoryProductCategories_spinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {


                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                            if(categoriesMap.values.toList()[position].equals("No category exists in this store!")){

                                Toast.makeText(this@InventoryActivity, "No product! Please add some", Toast.LENGTH_LONG).show()
                                fetchStoresProducts(0)

                            }else{
                                fetchStoresProducts(categoriesMap.keys.toList()[position])
                            }


                        }


                    }




                }



            }


        })

    }

    fun fetchStoresProducts(categoryID : Int){

        var URL = "http://$ipAddress:8000/storesproducts/$storeID&$categoryID"

        var client = OkHttpClient()


        var request = Request.Builder().url(URL).build()


        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {

                    Toast.makeText(this@InventoryActivity, "Error in Fetching Products $e", Toast.LENGTH_LONG).show()

                }

            }

            override fun onResponse(call: Call, response: Response) {

                try {
                    var body = response.body()?.string()


                    //Log.e("Stores Products ", "$body")
                    var gson = GsonBuilder().create()

                    var products = gson.fromJson(body, StoresProducts::class.java)

                    runOnUiThread {

                        try {

                            var inventoryAdapter = InventoryRecyclerAdapter(products)
                            inventory_recyclerView.adapter = inventoryAdapter

                        }catch (e: java.lang.Exception){
                            Log.e("Send Inventory Adapter", "$e")
                        }

                    }
                }catch (e: java.lang.Exception){
                    Log.e("Error fetch Inventory", "$e")
                }

            }


        })



    }


}

class StoresProducts(val products : List<StoreProduct>)

class StoreProduct(val Quantity: Int, val Product_Name: String, val ProductID: Int)
