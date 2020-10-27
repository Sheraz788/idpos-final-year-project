package com.example.sherazali.idpos_app.Salesman

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.Home.HomeActivity
import com.example.sherazali.idpos_app.R
import com.example.sherazali.idpos_app.Retailer.ProductRetailers
import com.example.sherazali.idpos_app.Retailer.Stores
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_create_saleman.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class CreateSaleman : AppCompatActivity() {

    var ipAddress : String? = null
    var retailerID : Int? = null
    var storeID : Int? = null
    var retailerUserID : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_saleman)


        ipAddress = Globals.ipAddress

        retailerUserID = Globals.returnUserID()
        addSalemanBtn.setOnClickListener {

            var nameValidation = false
            var emailValidation = false
            var personNameValidation = false
            var passwordValidation = false
            var imageValidation = false




            try {

                //Log.e("JSON", "${returnJSON()}")
                if (salemanUsername.length() < 6) {

                    salemanUsername.setError("User ID Atleast 6 words!")
                    nameValidation = false

                } else {
                    nameValidation = true
                }

                if (saleman_Name.length() < 6) {

                    saleman_Name.setError("Person Name Atleast 6 words!")
                    personNameValidation = false

                } else {
                    personNameValidation = true
                }

                if (!isValidEmail(saleman_Email.text.toString())) {

                    saleman_Email.setError("Enter valid email!")
                    emailValidation = false

                } else {
                    emailValidation = true
                }



                if (saleman_password.length() <= 0) {

                    saleman_password.setError("Enter password")
                    passwordValidation = false

                } else if (saleman_confirmPassword.text.toString() != saleman_password.text.toString()) {

                    saleman_confirmPassword.setError("Password didn't matched!")
                    passwordValidation = false

                } else {

                    passwordValidation = true

                }

            }catch (e: Exception){
                Log.e("Found Exception", "$e")
            }


            if(nameValidation && emailValidation && personNameValidation && passwordValidation){
                try{

                    postSaleMan()

                }catch (e: Exception){
                    Log.e("Exception", "$e")
                }
            }

        }
        fetchRetailersData()

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


        }else{
            super.onBackPressed()
            finish()
        }

    }

    fun isValidEmail(email:String) : Boolean{

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.isEmpty()
    }

    fun fetchRetailersData(){

        try {
            val URL = "http://$ipAddress:8000/retailer/$retailerUserID"


//            Log.e("Retailer ID", "$retailerUserID")

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

                        var retailerArrayAdapter = ArrayAdapter<String>(this@CreateSaleman, android.R.layout.simple_spinner_item, retailersList)
                        retailerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


                        salemanRetailer_spinner.adapter = retailerArrayAdapter

                        salemanRetailer_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


                                retailerID = retailerMap.keys.toList()[position]

                                fetchStores(retailerMap.keys.toList()[position])

                            }

                        }
                    }

                }


            })
        }catch (e: Exception){
            Log.e("Retailer Exception", "$e")
        }


    }

    fun fetchStores(retailerIDs: Int){

        var URL = "http://$ipAddress:8000/store/$retailerIDs"

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



                    var storeArrayAdapter = ArrayAdapter<String>(this@CreateSaleman, android.R.layout.simple_spinner_dropdown_item, storesList)


                    salemanStore_spinner.adapter = storeArrayAdapter


                    salemanStore_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {


                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {



                            if(storesMap.values.toList()[position].equals("No store for this retailer!")){
                                Toast.makeText(this@CreateSaleman, "Add Store then products!", Toast.LENGTH_SHORT).show()

                            }else
                            {
                                storeID = storesMap.keys.toList()[position]

                            }
                        }


                    }


                }



            }

        })
    }

    fun postSaleMan(){

        try {
            val JSON = MediaType.get("application/json; charset=utf-8")

            val json = returnSalemanJSON()


            val client = OkHttpClient()

            val URL = "http://$ipAddress:8000/create_user"


            val body = RequestBody.create(JSON, json)

            Log.e("JSON", "$json")
            val request = Request.Builder().url(URL).post(body).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                    runOnUiThread {
                        Toast.makeText(this@CreateSaleman, "Failed to Connect Check Internet", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {


                    if(response.code() == 200 && response.message() == "OK"){
                        runOnUiThread {

                            postSaleManDetail()

                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@CreateSaleman, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                        }
                    }
                }


            })





        }catch (e: Exception){

            Log.e("Error", "$e")

        }

    }


    //Post saleman's store and retailer detail

    fun postSaleManDetail(){

        try {
            val JSON = MediaType.get("application/json; charset=utf-8")

            val json = saleManDetail()


            val client = OkHttpClient()

            val URL = "http://$ipAddress:8000/create_saleman"


            val body = RequestBody.create(JSON, json)

            Log.e("JSON", "$json")
            val request = Request.Builder().url(URL).post(body).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {

                    runOnUiThread {
                        Toast.makeText(this@CreateSaleman, "Failed to Connect Check Internet", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {


                    if(response.code() == 200 && response.message() == "OK"){
                        runOnUiThread {
                            Toast.makeText(this@CreateSaleman, "Sale Man Added to Store ...", Toast.LENGTH_SHORT).show()
                            var homeIntent = Intent(this@CreateSaleman, HomeActivity::class.java)
                            startActivity(homeIntent)

                        }
                    }else{
                        runOnUiThread {
                            Toast.makeText(this@CreateSaleman, "Error: ${response.message()}", Toast.LENGTH_LONG).show()
                        }
                    }
                }


            })





        }catch (e: Exception){

            Log.e("Error", "$e")

        }







    }


    //create and return JSON data entered by the user
    fun returnSalemanJSON() : String{




        var username = salemanUsername.text.toString()
        var personname = saleman_Name.text.toString()
        var useremail = saleman_Email.text.toString()
        var userpassword = saleman_password.text.toString()

        //Getting User Type
        var checkedTypeID = userTypeRadioButton.checkedRadioButtonId
        var TypeRadioBtn = findViewById<RadioButton>(checkedTypeID)
        var getTypeValue = TypeRadioBtn.text.toString()





        //Setting Type R, C , S or D
        var Type = ""
         if(getTypeValue == "Saleman"){
            Type = "S"

        }



        return  "{\"UserID\" : \"$username\"," +
                "\"Name\" : \"$personname\"," +
                "\"Email\" : \"$useremail\"," +
                "\"Password\" : \"$userpassword\"," +
                "\"Type\" : \"$Type\"," +
                "\"Status\" : \"A\" }"

    }

    //Assigning Saleman against a specific retailer and store
    fun saleManDetail() : String{



        var username = salemanUsername.text.toString()

        return "{\"StoreID\" : \"$storeID\"," +
                "\"RetailerID\" : \"$retailerID\"," +
                "\"UserID\" : \"$username\"}"


    }


}
