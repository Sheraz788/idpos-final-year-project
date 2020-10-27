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
import com.example.sherazali.idpos_app.Users.CreateAccount
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_retails.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import java.lang.Exception

class RetailsActivity : AppCompatActivity() {

    var ipAddress = ""
    var retailerUserID : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retails)

        var global = Globals
        ipAddress = Globals.ipAddress
        try {
            retailerUserID = Globals.returnUserID()
            Log.e("Retailer ID ", "$retailerUserID")
        }catch (e: Exception){
            Log.e("Retailer ID ", "$e")
        }

        //retailersRcyclerView.layoutManager = LinearLayoutManager(this)

        var retailersList = ArrayAdapter.createFromResource(this,
            R.array.retailer_spinner, android.R.layout.simple_spinner_item)
        retailersList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        retailers_mainspinner.adapter = retailersList




        fetchRetailersData()


        addRetailer_Btn.setOnClickListener {

            var addRetailerIntent = Intent(this, CreateRetailer::class.java)
            startActivity(addRetailerIntent)


        }

        back_arrow.setOnClickListener {
            val home_intent = Intent(this, HomeActivity::class.java)
            startActivity(home_intent)
            overridePendingTransition(
                R.anim.trans_left_in,
                R.anim.trans_right_out
            )
            finish()
        }


    }

    override fun onBackPressed() {


        if(Globals.userType == "R"){
            val home_intent = Intent(this, HomeActivity::class.java)
            startActivity(home_intent)
            overridePendingTransition(
                R.anim.trans_left_in,
                R.anim.trans_right_out
            )
            finish()
        }else {
            super.onBackPressed()
            finish()
        }
    }

    fun fetchRetailersData(){

        try {
            val URL = "http://$ipAddress:8000/retailer/$retailerUserID"

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

                        var retailerArrayAdapter = ArrayAdapter<String>(this@RetailsActivity, android.R.layout.simple_spinner_item, retailersList)
                        retailerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


                        retailers_mainspinner.adapter = retailerArrayAdapter

                        retailers_mainspinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


                            if(parent?.getItemAtPosition(position)!!.equals("Select a Retailer!")){

                            }else{
                                fetchRetailerDetails(retailerMap.keys.toList()[position])
                            }



                            }

                        }




                    }



                }


            })
        }catch (e: Exception){
            Log.e("Retailer Exception", "$e")
        }


    }


    fun fetchRetailerDetails(retailerID: Int){

        try{

            var URL = "http://$ipAddress:8000/retailerdetails/$retailerID"

            var client = OkHttpClient()

            var request = Request.Builder().url(URL).build()

            client.newCall(request).enqueue(object : Callback{


                override fun onFailure(call: Call, e: IOException) {

                    runOnUiThread {

                        Toast.makeText(this@RetailsActivity, "Error in Fetching Retailer Details", Toast.LENGTH_SHORT).show()


                    }


                }

                override fun onResponse(call: Call, response: Response) {

                    try {

                        var body = response.body()?.string()

                        var jsonArray = JSONArray(body)
                        var jsonObject = jsonArray.getJSONObject(0)


                        var retailerName = jsonObject.getString("RetailerName")
                        var retailerOwner = jsonObject.getString("OwnerName")
                        var retailerTelephone = jsonObject.getString("Telephone")
                        var retailerMobile = jsonObject.getString("Mobile")
                        var retailerAddress = jsonObject.getString("Address")
                        var retailerEmail = jsonObject.getString("Email")

                        runOnUiThread {

                            retailName_txtValueRetails.text = retailerName
                            ownerName_txtValueRetails.text = retailerOwner
                            telephone_txtValueRetails.text = retailerTelephone
                            mobile_txtValueRetails.text = retailerMobile
                            address_txtValueRetails.text = retailerAddress


                        }


//                        Log.e("Retailer Detail", "$retailerName, $retailerOwner, $retailerAddress, $retailerMobile, $retailerTelephone")


                    }catch (e: Exception){

                        Log.e("Response Exception", "$e")

                    }






                }


            })




        }catch (e: Exception){

            Log.e("Retailer Details Error", "$e")

        }



    }

}

class Retailers(val retailers : List<Retailer>)

class Retailer(var RetailerID: Int, var RetailerName: String)

