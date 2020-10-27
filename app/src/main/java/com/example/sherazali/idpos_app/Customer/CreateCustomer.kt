package com.example.sherazali.idpos_app.Customer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.R
import com.example.sherazali.idpos_app.Users.LoginActivity
import kotlinx.android.synthetic.main.activity_create_customer.*
import okhttp3.*
import java.io.IOException

class CreateCustomer : AppCompatActivity() {

    var ipAddress = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_customer)


        var getCustomerIntent = intent
        var userID = getCustomerIntent.getStringExtra("username")
        customerUserName.text = userID

        var global = Globals
        ipAddress = Globals.ipAddress


        //Setting Values
        customerUserName.setText(intent.getStringExtra("USERID"))
        customer_name.setText(intent.getStringExtra("CUSTOMERNAME"))
        customer_email.setText(intent.getStringExtra("USEREMAIL"))

        createCustomer_Btn.setOnClickListener {


            var customer_nameValidation = false
            var customer_telephoneValidation = false
            var mobile_numberValidation = false
            var customer_emailValidation = false
            var customer_addressValidation = false

            try {

                //Log.e("JSON", "${returnJSON()}")
                if (customer_name.length() < 6) {

                    customer_name.setError("Name Atleast of 6 words!")
                    customer_nameValidation = false

                } else {
                    customer_nameValidation = true
                }

                if (!isValidEmail(customer_email.text.toString())) {

                    customer_email.setError("Enter valid email!")
                    customer_emailValidation = false

                } else {
                    customer_emailValidation = true
                }



                if (customer_telephone.length() <= 0 && customer_telephone.length() != 11) {

                    customer_telephone.setError("Enter telephone number of 11 digits")
                    customer_telephoneValidation = false

                }else {

                    customer_telephoneValidation = true

                }

                if (customer_mobile_number.length() <= 0 && customer_mobile_number.length() != 11) {

                    customer_mobile_number.setError("Enter mobile number of 11 digits")
                    mobile_numberValidation = false

                }else {

                    mobile_numberValidation = true

                }


                if (customer_address.length() <= 6) {

                    customer_address.setError("Enter valid address atleast 6 words")
                    customer_addressValidation = false

                }else {

                    customer_addressValidation = true

                }


                if(customer_addressValidation && customer_emailValidation && customer_emailValidation && customer_nameValidation && mobile_numberValidation){

                    postUser()



                }

            }catch (e: Exception){
                Log.e("Found Exception", "$e")
            }




        }

    }


    fun isValidEmail(email: String) : Boolean{

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.isEmpty()

    }

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
                        Toast.makeText(this@CreateCustomer, "Failed to Connect Check Internet", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {


                    if(response.code() == 200 && response.message() == "OK"){
                        runOnUiThread {
                            //Toast.makeText(this@CreateCustomer, "Creating User ...", Toast.LENGTH_SHORT).show()
                            postCustomer()
                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@CreateCustomer, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                        }
                    }
                }


            })





        }catch (e: java.lang.Exception){

            Log.e("Error", "$e")

        }







    }


    //post customer on Server
    fun postCustomer(){

        var customerJSON = MediaType.get("application/json; charset=utf-8")

        var customerjson = returnCustomerJSON()


        var client = OkHttpClient()

        //var URL = "http://192.168.43.2:8000/create_customer"
        var URL = "http://$ipAddress:8000/create_customer"

        var body = RequestBody.create(customerJSON,  customerjson)

        var request = Request.Builder().url(URL).post(body).build()

        client.newCall(request).enqueue(object : Callback{


            override fun onFailure(call: Call, e: IOException) {

                Toast.makeText(applicationContext, "Customer Account Failure", Toast.LENGTH_LONG).show()
                Log.e("Customer Failure", "$e")

            }

            override fun onResponse(call: Call, response: Response) {

                if(response.message() == "OK" && response.code() == 200){

                    runOnUiThread {

                        Toast.makeText(applicationContext, "Customer Account Created", Toast.LENGTH_LONG).show()

                        var loginIntent = Intent(this@CreateCustomer, LoginActivity::class.java)
                        startActivity(loginIntent)
                        finish()

                    }


                }else{

                    runOnUiThread {
                        Toast.makeText(applicationContext, "Customer Account is not Created", Toast.LENGTH_LONG).show()

                    }
                }



            }


        })



    }

    //create and Return JSON data entered by the

    fun returnCustomerJSON() : String{


        var userID = intent.getStringExtra("USERID")
        customerUserName.text = userID
        var customername = customer_name.text.toString()
        var customertelephone = customer_telephone.text.toString()
        var customermobile = customer_mobile_number.text.toString()
        var customeremail = customer_email.text.toString()
        var customeraddress = customer_address.text.toString()




        return "{\"Name\": \"$customername\"," +
                "\"Mobile\" : \"$customermobile\"," +
                "\"Telephone\" : \"$customertelephone\"," +
                "\"Address\" : \"$customeraddress\"," +
                "\"Email\" : \"$customeremail\"," +
                "\"UserID\" : \"$userID\"," +
                "\"Status\" : \"A\"" +
                "}"


    }


}
