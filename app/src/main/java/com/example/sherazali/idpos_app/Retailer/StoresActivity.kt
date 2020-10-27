package com.example.sherazali.idpos_app.Retailer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.Home.HomeActivity
import com.example.sherazali.idpos_app.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_stores.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.lang.Exception

class StoresActivity : AppCompatActivity() {

    var ipAddress = ""
    var retaileruserID : String? = null
    var retailerBundle = Bundle()
    var retailerName = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stores)


        var globals =  Globals.chooseShopDetails

        ipAddress = globals.ipAddress

        retaileruserID = globals.returnUserID()


        var storesList = ArrayAdapter.createFromResource(this,
            R.array.store_spinner, android.R.layout.simple_spinner_item)
        storesList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        stores_mainspinner.adapter = storesList
        addStore_Btn.setOnClickListener {

            var addStoreIntent = Intent(this, CreateStore::class.java)
            addStoreIntent.putExtras(retailerBundle)
            startActivity(addStoreIntent)

        }

        back_arrow.setOnClickListener {

            var homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out)

            finish()

        }

//        fetchStores()
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

//        Log.e("URL", "http://$ipAddress:8000/retailer/$retailerID")

        retailerBundle.putString("userid", "$retaileruserID")

        try {
            val URL = "http://$ipAddress:8000/retailer/$retaileruserID"

            val client = OkHttpClient()


            val request = Request.Builder().url(URL).build()


            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {


                }

                override fun onResponse(call: Call, response: Response) {


                    var body = response.body()?.string()

                    var gson = GsonBuilder().create()

                    var retailers = gson.fromJson(body, Retailers::class.java)

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
                        //Log.e("retailers List", "$retailersList")

                        var retailerArrayAdapter = ArrayAdapter<String>(this@StoresActivity, android.R.layout.simple_spinner_item, retailersList)
                        retailerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


                        retailers_mainspinner.adapter = retailerArrayAdapter

                        retailers_mainspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


                                if(parent?.getItemAtPosition(position)!!.equals("Select a Retailer!")){

                                }else{
                                    fetchStores(retailerMap.keys.toList()[position])

                                    retailerName = retailerMap.values.toList()[position]

                                    retailerBundle.putString("retailerid","${retailerMap.keys.toList()[position]}")

                                }



                            }

                        }




                    }



                }


            })
        }catch (e: Exception){
            Log.e("Store Exception", "$e")
        }


    }

    fun fetchStores(retailerID : Int){


        try {


            val URL = "http://$ipAddress:8000/store/$retailerID"

            val client = OkHttpClient()


            val request = Request.Builder().url(URL).build()


            client.newCall(request).enqueue(object : Callback{

                override fun onFailure(call: Call, e: IOException) {

                    runOnUiThread {

                        Toast.makeText(this@StoresActivity, "Fetching Stores Error $e", Toast.LENGTH_LONG).show()

                    }

                }

                override fun onResponse(call: Call, response: Response) {

                    var body = response.body()?.string()






                    val gson = GsonBuilder().create()

                    val stores = gson.fromJson(body, Stores::class.java)



                    runOnUiThread {


                        var storesMap = HashMap<Int, String>()

                        if(stores.stores.isEmpty()){

                            storesMap.put(1, "No Store Exists For This Retailer!")

                        }else{
                            for (store in stores.stores){

                                storesMap.put(store.StoreID, store.StoreName)

                            }
                        }



                        var storesList : List<String> = ArrayList()


                        for(store in storesMap){


                                storesList += store.value

                        }


                        var storeArrayAdapter = ArrayAdapter<String>(this@StoresActivity,android.R.layout.simple_spinner_item, storesList)

                        storeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                        stores_mainspinner.adapter = storeArrayAdapter

                        stores_mainspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                                if(storesMap.values.toList()[position].equals("No Store Exists For This Retailer!")){
                                    Toast.makeText(this@StoresActivity, "No Store Exists!", Toast.LENGTH_SHORT).show()

                                }else{
                                    fetchStoresDetail(storesMap.keys.toList()[position])
                                }

                            }


                        }





                    }





                }


            })


        }catch (e: Exception){


            Log.e("Fetching Store Error", "$e")


        }


    }


    fun fetchStoresDetail(storeID: Int){

        var URL = "http://$ipAddress:8000/storedetails/$storeID"


        var client = OkHttpClient()

        var request = Request.Builder().url(URL).build()


        client.newCall(request).enqueue(object : Callback{

            override fun onFailure(call: Call, e: IOException) {


            }

            override fun onResponse(call: Call, response: Response) {

                var body = response.body()?.string()


                var storeArray = JSONArray(body)
                var storeObject = storeArray.getJSONObject(0)



                runOnUiThread {

                    storeName_txtValueStores.text = storeObject.getString("StoreName")
                    ownerName_txtValueStores.text = retailerName
                    telephone_txtValueStores.text = storeObject.getString("Telephone")
                    mobile_txtValueStores.text = storeObject.getString("Mobile")
                    address_txtValueStores.text = storeObject.getString("Address")

                }

            }


        })




    }
}

class Stores(val stores : List<Store>)


class Store(val StoreID: Int, val StoreName: String)

class StoresTrends(val trends : List<StoreTrend>)

class StoreTrend (val ProductCount: Int, val ProductName: String, val CategoryName: String)

class StoresSuggestions(val suggestions : List<StoreSuggestion>)

class StoreSuggestion (val ProductCount: Int, val ProductName: String, val CategoryName: String)






