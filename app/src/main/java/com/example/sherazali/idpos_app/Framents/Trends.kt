package com.example.sherazali.idpos_app.Framents

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.example.sherazali.idpos_app.Adapters.TrendsAdapter
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.R
import com.example.sherazali.idpos_app.Retailer.Stores
import com.example.sherazali.idpos_app.Retailer.StoresTrends
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class  Trends : Fragment(){

    var trendsSpinner : Spinner? = null
    var trendsRecyclerView : RecyclerView? = null
    var ipAddress : String? = null
    var userID : String? = null
    var activityContext : Context? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.stores_trends, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

         trendsSpinner = view.findViewById(R.id.stores_trends_spinner)

        trendsRecyclerView = view.findViewById(R.id.stores_trends_recyclerview)

        ipAddress = Globals.ipAddress
        userID = Globals.returnUserID()

        activityContext = this.activity

        trendsRecyclerView!!.layoutManager = LinearLayoutManager(activity)

        fetchStores()


    }

    fun fetchStores(){

        var URL = ""

        if(Globals.userType == "R"){
            URL = "http://$ipAddress:8000/fetch_stores/$userID"

            Log.e("URL","$URL")
        }else{
            URL = "http://$ipAddress:8000/saleman_stores/$userID"
        }


        var client = OkHttpClient()


//        retailerID = retailerIDs


        var request = Request.Builder().url(URL).build()


        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {


            }

            override fun onResponse(call: Call, response: Response) {

                var body = response.body()?.string()

                 //Log.e("Stores", "$body")

                var gson = GsonBuilder().create()


                var stores = gson.fromJson(body, Stores::class.java)


                try {
                    activity?.runOnUiThread {


                        var storesMap = HashMap<Int, String>()
                        var storesList = ArrayList<String>()
                       // Log.e("Stores", "$body")
                        if (stores.stores.isEmpty()) {

                            storesMap.put(1, "No store for this retailer!")


                        } else {

                            for (store in stores.stores) {

                                // storesList.add(store.StoreName)
                                storesMap.put(store.StoreID, store.StoreName)

                            }
                        }


                        for (storeNames in storesMap) {
                            storesList.add(storeNames.value)
                        }

                        var storeArrayAdapter = ArrayAdapter<String>(
                            activityContext,
                            android.R.layout.simple_spinner_dropdown_item,
                            storesList
                        )


                        trendsSpinner!!.adapter = storeArrayAdapter


                        trendsSpinner!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {


                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


                                if (storesMap.values.toList()[position].equals("No store for this retailer!")) {
                                    Toast.makeText(activityContext, "Add Store then products!", Toast.LENGTH_SHORT)
                                        .show()


                                } else {

                                    //storeID = storesMap.keys.toList()[position]

                                    fetchTrends(storesMap.keys.toList()[position])
                                    // fetchRetailersData(storeID!!)

                                }


                            }


                        }


                    }
                }catch (e: Exception){
                    Log.e("Fragment Exception", "$e")
                }



            }

        })




    }


    fun fetchTrends(storeID : Int){


        var URL = "http://$ipAddress:8000/stores_trends/$storeID"

        var client = OkHttpClient()


//        retailerID = retailerIDs


        var request = Request.Builder().url(URL).build()


        client.newCall(request).enqueue(object : Callback{


            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {

                var body = response.body()?.string()

                var gson = GsonBuilder().create()

                var storestrends = gson.fromJson(body, StoresTrends::class.java)


                activity?.runOnUiThread {

                    trendsRecyclerView!!.adapter = TrendsAdapter(storestrends)


                }


            }


        })


    }



}