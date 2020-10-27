package com.example.sherazali.idpos_app.Retailer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.R
import kotlinx.android.synthetic.main.activity_create_store.*
import okhttp3.*
import java.io.IOException

class CreateStore : AppCompatActivity() {

    var ipAddress = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_store)


        var globals = Globals.chooseShopDetails
        ipAddress = globals.ipAddress

        CreateStore_Btn.setOnClickListener {

            var storename_nameValidation = false
            var store_telephoneValidation = false
            var mobile_numberValidation = false
            var store_addressValidation = false

            try {

                //Log.e("JSON", "${returnJSON()}")
                if (store_name.length() < 6) {

                    store_name.setError("Name Atleast of 6 words!")
                    storename_nameValidation= false

                } else {
                    storename_nameValidation = true
                }

                if (store_telephone.length() <= 0 && store_telephone.length() != 11) {

                    store_telephone.setError("Enter telephone number of 11 digits")
                    store_telephoneValidation = false

                }else {

                    store_telephoneValidation = true

                }

                if (mobile_number.length() <= 0 && mobile_number.length() != 11) {

                    mobile_number.setError("Enter mobile number of 11 digits")
                    mobile_numberValidation = false

                }else {

                    mobile_numberValidation = true

                }


                if (store_address.length() <= 6) {

                    store_address.setError("Enter valid address atleast 6 words")
                    store_addressValidation = false

                }else {

                    store_addressValidation = true

                }


                if(store_addressValidation && store_telephoneValidation && storename_nameValidation && mobile_numberValidation){


                    postStoreData()



                }

            }catch (e: Exception){
                Log.e("Found Exception", "$e")
            }


        }



    }

    override fun onBackPressed() {
        super.onBackPressed()

        val stores_intent = Intent(this, StoresActivity::class.java)
        startActivity(stores_intent)
        overridePendingTransition(
            R.anim.trans_left_in,
            R.anim.trans_right_out
        )
        finish()

    }


    fun postStoreData(){

        var jsonType = MediaType.get("application/json; charset=utf-8")


        var URL = "http://$ipAddress:8000/create_store"

        var storeJSON = returnStoreJSON()


        var bodyRequest = RequestBody.create(jsonType, storeJSON)


        var request = Request.Builder().url(URL).post(bodyRequest).build()


        var client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback{

            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {

                    Toast.makeText(this@CreateStore, "Store is Not Created: $e", Toast.LENGTH_LONG).show()

                }
            }

            override fun onResponse(call: Call, response: Response) {


                runOnUiThread {

                    if(response.code() == 200 && response.message() == "OK"){
                        Toast.makeText(this@CreateStore, "Store Created For Retailer!", Toast.LENGTH_SHORT).show()

                        var storeActivityIntent = Intent(this@CreateStore, StoresActivity::class.java)
                        startActivity(storeActivityIntent)

                    }else{

                        Toast.makeText(this@CreateStore, "Error in Store Creation! ${response.message()}", Toast.LENGTH_LONG).show()

                    }


                }

            }

        })


        Log.e("Store JSON", "$storeJSON")


    }


    fun returnStoreJSON() : String{

        var getIntentFromCreateStore = intent
        var retailerID = getIntentFromCreateStore.extras.getString("retailerid")
        var userID = getIntentFromCreateStore.extras.getString("userid")

        Log.e("Bundle Retailer ID", "$retailerID")

        var storename = store_name.text.toString()
        var storeaddress = store_address.text.toString()
        var storetelephone = store_telephone.text.toString()
        var storemobile = mobile_number.text.toString()







        return  "{\"StoreName\" : \"$storename\"," +
                "\"Address\" : \"$storeaddress\"," +
                "\"Telephone\" : \"$storetelephone\"," +
                "\"Mobile\" : \"$storemobile\"," +
                "\"Status\" : \"A\"," +
                "\"RetailerID\" : \"$retailerID\"," +
                "\"UserID\" : \"$userID\"}"

    }

}
