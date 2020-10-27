package com.example.sherazali.idpos_app.Retailer

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.util.Log
import android.widget.Toast
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.R
import com.example.sherazali.idpos_app.Users.LoginActivity
import kotlinx.android.synthetic.main.activity_create_retailer.*
import okhttp3.*
import java.io.IOException

class CreateRetailer : AppCompatActivity() {

    var ipAddress = ""
    var userID : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_retailer)


        val globals = Globals
        ipAddress = Globals.ipAddress

        //Setting Values
        var retaileremail = intent.getStringExtra("USEREMAIL")
        var ownername = intent.getStringExtra("OWNERNAME")

        retailer_email.setText(retaileremail)
        owner_name.setText(ownername)


        CreateRetailer_Btn.setOnClickListener {



            var retailer_nameValidation = false
            var owner_nameValidation = false
            var retailer_telephoneValidation = false
            var mobile_numberValidation = false
            var retailer_emailValidation = false
            var retailer_addressValidation = false

            try {

                //Log.e("JSON", "${returnJSON()}")
                if (retailer_name.length() < 6) {

                    retailer_name.setError("Name Atleast of 6 words!")
                    retailer_nameValidation = false

                } else {
                    retailer_nameValidation = true
                }

                if (owner_name.length() < 6) {

                    owner_name.setError("Owner Name Atleast of 6 words!")
                    owner_nameValidation = false

                } else {
                    owner_nameValidation = true
                }

                if (!isValidEmail(retailer_email.text.toString())) {

                    retailer_email.setError("Enter valid email!")
                    retailer_emailValidation = false

                } else {
                    retailer_emailValidation = true
                }



                if (retailer_telephone.length() <= 0 && retailer_telephone.length() != 11) {

                    retailer_telephone.setError("Enter telephone number of 11 digits")
                    retailer_telephoneValidation = false

                }else {

                    retailer_telephoneValidation = true

                }

                if (mobile_number.length() <= 0 && mobile_number.length() != 11) {

                    mobile_number.setError("Enter mobile number of 11 digits")
                    mobile_numberValidation = false

                }else {

                    mobile_numberValidation = true

                }


                if (retailer_address.length() <= 6) {

                    retailer_address.setError("Enter valid address atleast 6 words")
                    retailer_addressValidation = false

                }else {

                    retailer_addressValidation = true

                }


                if(retailer_addressValidation && retailer_emailValidation && retailer_telephoneValidation && retailer_nameValidation && owner_nameValidation && mobile_numberValidation){


                    if(Globals.userType == "R"){

                        userID = Globals.returnUserID()
                        postRetailer()


                    }else{
                        userID = intent.getStringExtra("USERID")
                        postUser()
                    }




                }

            }catch (e: Exception){
                Log.e("Found Exception", "$e")
            }




        }


    }

    override fun onBackPressed() {
        super.onBackPressed()

        if(Globals.userType == "R"){
            val retailer_intent = Intent(this, RetailsActivity::class.java)
            startActivity(retailer_intent)
            overridePendingTransition(
                R.anim.trans_left_in,
                R.anim.trans_right_out
            )
            finish()
        }else{
            super.onBackPressed()
            finish()
        }

    }

    fun isValidEmail(email: String) : Boolean{

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.isEmpty()

    }

    //Post User
    //post retailer user on Server

    fun postUser(){



        try {
            val JSON = MediaType.get("application/json; charset=utf-8")

            val json = intent.getStringExtra("USERINFO")


            val client = OkHttpClient()

            val URL = "http://$ipAddress:8000/create_user"


            val body = RequestBody.create(JSON, json)

            Log.e("JSON", "$json")
            val request = Request.Builder().url(URL).post(body).build()

            client.newCall(request).enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {

                    runOnUiThread {
                        Toast.makeText(this@CreateRetailer, "Failed to Connect Check Internet", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {


                    if(response.code() == 200 && response.message() == "OK"){
                        runOnUiThread {
                            Toast.makeText(this@CreateRetailer, "Creating User ...", Toast.LENGTH_SHORT).show()
                            postRetailer()
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@CreateRetailer, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                        }
                    }
                }


            })





        }catch (e: java.lang.Exception){

            Log.e("Error", "$e")

        }







    }

    //Post Retailer on the Server

    fun postRetailer(){

        val JSON = MediaType.get("application/json; charset=utf-8")

        val json = returnRetailerJSON()

//        Log.e("JSON String", "$json")

        val client = OkHttpClient()



        val URL = "http://$ipAddress:8000/create_retailer"

        var body = RequestBody.create(JSON, json)

        var request = Request.Builder().url(URL).post(body).build()

        client.newCall(request).enqueue(object : Callback{

            override fun onFailure(call: Call, e: IOException) {

            runOnUiThread {

                Toast.makeText(this@CreateRetailer, "Internet is not Connected .. $e", Toast.LENGTH_LONG).show()
            }


            }

            override fun onResponse(call: Call, response: Response) {


                if(response.code() == 200 && response.message() == "OK"){

                    runOnUiThread {
                        Toast.makeText(this@CreateRetailer, "Retailer Account Created", Toast.LENGTH_LONG).show()

                        if(Globals.userType == "R"){
                            var retailsActivityIntent = Intent(this@CreateRetailer, RetailsActivity::class.java)
                            startActivity(retailsActivityIntent)
                            finish()
                        }else{
                            var loginActivityIntent = Intent(this@CreateRetailer, LoginActivity::class.java)
                            startActivity(loginActivityIntent)
                            finish()
                        }

                    }



                }else{

                    runOnUiThread {

                        Toast.makeText(this@CreateRetailer, "Retailer Account is not Created", Toast.LENGTH_LONG).show()

                    }



                }
            }

        })




    }


    //create and return JSON data entered by the Retailer
    fun returnRetailerJSON() : String{




        var retailerName = retailer_name.text.toString()
        var ownerName = owner_name.text.toString()
        var telephoneNo = retailer_telephone.text.toString()
        var mobileNo = mobile_number.text.toString()
        var emailAddress = retailer_email.text.toString()
        var address = retailer_address.text.toString()
        var category = 1

        return "{\"RetailerName\" : \"$retailerName\"," +
                "\"OwnerName\" : \"$ownerName\"," +
                "\"Category\" : " + category + "," +
                "\"Telephone\" : \"$telephoneNo\"," +
                "\"Mobile\" : \"$mobileNo\"," +
                "\"Email\" : \" $emailAddress\"," +
                "\"Address\" : \"$address\"," +
                "\"Status\" : \"A\"," +
                "\"UserID\" : \"$userID\"" +
                "}"









    }



}
