package com.example.sherazali.idpos_app.Retailer

import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.*
import com.example.sherazali.idpos_app.Adapters.TransactionRecyclerAdapter
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.Home.HomeActivity
import com.example.sherazali.idpos_app.R
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.activity_transaction.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.jar.Manifest
import kotlin.math.log

class TransactionActivity : AppCompatActivity(), TransactionRecyclerAdapter.transactionCallBack {


    var ipAddress  = ""
    var storeID : Int? = null
    var retailerID : Int? = null
    var customerID : Int? = null
    var customerName: String? = null
    var customerMobile : String? = null
    var customerBuyedProducts : ArrayList<String> = ArrayList()
    var userID : String? = null
    var productCodeList = ArrayList<String>()
    var hashMap : HashMap<String, Int> = HashMap()
    var productHashmap : HashMap<String, ArrayList<String> > = HashMap()

    var transactionAdapter : TransactionRecyclerAdapter? = null

    var transactionProductMap : HashMap<Int, ArrayList<String>> = HashMap()
    var checkMap : String = ""//  HashMap<Int, ArrayList<String>> = HashMap()
    lateinit var progressbar_loading : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)


        progressbar_loading = Dialog(this)

        var global = Globals
        ipAddress = global.ipAddress
        userID = global.returnUserID()

        productTransaction_recycler.layoutManager = LinearLayoutManager(this)




        back_arrow.setOnClickListener {
            val home_intent = Intent(this, HomeActivity::class.java)
            startActivity(home_intent)
            overridePendingTransition(

                R.anim.trans_left_in,
                R.anim.trans_right_out
            )
            finish()
        }


        fetchStores()


        //Add product detail for transaction
        addTransactionProduct_Btn.setOnClickListener {
            var getBarcode = productTransaction_barcode.text.toString()

            if(getBarcode.isEmpty()){

                productTransaction_barcode.error = "Scan Product!"

            }else{

                fetchProductForTransaction(getBarcode)
            }
        }


        //Checking the transaction type which one is selected
        var TxnRadioBtn = transactionRadioTypeBtn.checkedRadioButtonId
        var selectedRadioBtnType = findViewById<RadioButton>(TxnRadioBtn)
        var getSelectedValue = selectedRadioBtnType.text.toString()

        var transactionType = ""

        if (getSelectedValue == "Sale") {
            transactionType = "S"

        } else {

            transactionType = "P"

        }


        //When Click on the Checkout Btn
        transactionCheckout_Btn.setOnClickListener {

            progressbar_loading.setContentView(R.layout.progressbar_loading)
            fetchCustomerID()

            var dataPosted = false


            if (customerID == 0) {

                transactionCustomer_ID.error = "No user exists! Try again"
               // progressbar_loading.dismiss()
                transactionCustomer_ID.isFocusable = true
                progressbar_loading.dismiss()

            } else if (transactionCustomer_ID.text.toString().isEmpty()) {

                transactionCustomer_ID.setError("Please Enter Customer Username!")

                transactionCustomer_ID.isFocusable = true
                progressbar_loading.dismiss()
            } else {

                var progressbar_TextView = progressbar_loading.findViewById(R.id.progressBar_txtView) as TextView

                progressbar_TextView.text = "Adding transaction ... "

                progressbar_loading.setCancelable(false)
                progressbar_loading.setCanceledOnTouchOutside(false)
                progressbar_loading.show()

                Handler().postDelayed({

                    if(transactionProductMap.values.isEmpty()){

                        Toast.makeText(this, "Please add at least one product!", Toast.LENGTH_LONG).show()

                    }else{
                        for ((key, value) in transactionProductMap) {

                            customerBuyedProducts = value

                            //fetchCustomerID(userID)
                            Log.e("Inside the LOOP", "LOOP")

                            var saledProductsJSON = "{\"TxnType\" : \"$transactionType\"," +
                                    "\"ProductID\" : \"$key\"," +
                                    "\"CustomerID\" : \"$customerID\"," +
                                    "\"StoreID\" : \"$storeID\"," +
                                    "\"RetailerID\" : \"$retailerID\"," +
                                    "\"DistributorID\" : \"1\"," +
                                    "\"Quantity\" : \"${value[1]}\"," +
                                    "\"Price\" : \"${value[2]}\", " +
                                    "\"UOM\" : \"4\"," +
                                    "\"UserID\" : \"$userID\"," +
                                    "\"Status\" : \"A\" }"


                            addTransaction(saledProductsJSON)

                            progressbar_loading.dismiss()

                            // Log.e("Saled Products", "$saledProductsJSON")

                            dataPosted = true

                        }


                    }

                    if (dataPosted) {


                        try {

                            if(ContextCompat.checkSelfPermission(this@TransactionActivity, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED){
                                ActivityCompat.requestPermissions(this@TransactionActivity, arrayOf(android.Manifest.permission.SEND_SMS), 1 )
                            }

                            else{
                                sendSMS()
                            }


                        }catch (e:Exception){
                            Log.e("Contact Error", "$e")
                        }
                        Handler().postDelayed({

                            checkStoreInventory()


                        }, 3000)

                           productTransaction_barcode.text.clear()
                           transactionCustomer_ID.text.clear()

                           //Clear Collections
                           productHashmap.clear()
                           hashMap.clear()

                           //Refreshing the adapter
                           transactionAdapter?.notifyDataSetChanged()




                        noProductScaned_textView.visibility = View.VISIBLE
                        totalTransactionPriceLinearLayout.visibility = View.INVISIBLE
                        productTransaction_barcode.isFocusable = true
                        // transactionCustomer_ID.isFocusable = false
                        transactionAdapter = TransactionRecyclerAdapter(this, productHashmap, hashMap)

                        productTransaction_recycler.adapter = transactionAdapter


                        Log.e("Adapter Updated", "Adapter Changed")
                    } else {

                        Log.e("Adapter not Changed", "Adapter $dataPosted")
                        progressbar_loading.dismiss()
                    }


                }, 2000)


            }


        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 1 ){
            if(grantResults.size > 0){
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS ) == PackageManager.PERMISSION_GRANTED){
                   sendSMS()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    fun sendSMS(){

        try{
            var pendingIntent = PendingIntent.getActivity(this@TransactionActivity, 0, Intent(this@TransactionActivity, TransactionActivity::class.java), 0)
            var sendTransaction = SmsManager.getDefault()
            var messageTemplate = ""
            var productsList = ""

            for(items in customerBuyedProducts){
                productsList += items
            }

            Log.e("Product List", "$productsList")

            sendTransaction.sendTextMessage("$customerMobile", null, "Your Name is $customerName. You Bought these products $productsList.", pendingIntent, null)
        }catch (e:java.lang.Exception){
            Log.e("Sending SMS Exception", "$e")
        }


    }


    override fun transactionDone(transactionProductID: String, transactionProductDescription: String, transactionProductQuantity: String, transactionProductPrice: String
    ) {

       // Log.e("Transacted Products", "$transactionProductID, $transactionProductDescription, $transactionProductQuantity, $transactionProductPrice")


        var saledProductsList = ArrayList<String>()
        saledProductsList.add(transactionProductDescription)
        saledProductsList.add(transactionProductQuantity)
        saledProductsList.add(transactionProductPrice)

        transactionProductMap.put(transactionProductID.toInt(), saledProductsList)



        var totalPrice = 0

        if(checkMap != transactionProductMap.toString()) {

//            Log.e("Map", "one time $transactionProductMap")

            for((key, value) in transactionProductMap){

//
//                    Log.e("$key", "${value[2]}")

                totalPrice += value[2].toInt()

//                Log.e("Total Price", "$totalPrice")

                totalTransaction_price.text = totalPrice.toString()

            }

        }


        checkMap = transactionProductMap.toString()
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

               // Log.e("Stores", "$body")

                var gson = GsonBuilder().create()


                var stores = gson.fromJson(body, Stores::class.java)


                runOnUiThread {


                    var storesMap = HashMap<Int, String>()
                    var storesList = ArrayList<String>()

                    if(stores.stores.isEmpty()){

                        storesMap.put(1,"No store for this retailer!")


                    }else{

                        for(store in stores.stores){

                           // storesList.add(store.StoreName)
                            storesMap.put(store.StoreID, store.StoreName)

                        }
                    }


                    for(storeNames in storesMap){
                        storesList.add(storeNames.value)
                    }

                    var storeArrayAdapter = ArrayAdapter<String>(this@TransactionActivity, android.R.layout.simple_spinner_dropdown_item, storesList)


                    transactionSelectStore.adapter = storeArrayAdapter


                    transactionSelectStore.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {


                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {



                            if(storesMap.values.toList()[position].equals("No store for this retailer!")){
                                Toast.makeText(this@TransactionActivity, "Add Store then products!", Toast.LENGTH_SHORT).show()


                            }else
                            {

                                storeID = storesMap.keys.toList()[position]

                                fetchRetailersData(storeID!!)

                            }




                        }


                    }


                }



            }

        })




    }

    fun fetchRetailersData(storeID: Int){

        try {
            val URL = "http://$ipAddress:8000/storeretailer/$storeID"



            val client = OkHttpClient()


            val request = Request.Builder().url(URL).build()


            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {


                }

                override fun onResponse(call: Call, response: Response) {


                    var body = response.body()?.string()



                   try{
                       var jsonArray = JSONArray(body)
                       var jsonObject = jsonArray.getJSONObject(0)

                        retailerID = jsonObject.getInt("RetailerID")


                   }catch (e: java.lang.Exception){

                       Log.e("Exception", "$e")

                   }

                    }






            })
        }catch (e: Exception){
            Log.e("Retailer Exception", "$e")
        }


    }

    fun fetchProductForTransaction(barcode : String){
        var URL = "http://$ipAddress:8000/productdetail/$storeID&$barcode"
        var client = OkHttpClient()
        var request = Request.Builder().url(URL).build()
        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                var body = response.body()?.string()
                var gson = GsonBuilder().create()
                var products = gson.fromJson(body, Products::class.java)
                runOnUiThread {
                    try {
                        if (products.products.count() == 0) {
                            Toast.makeText(
                                this@TransactionActivity,
                                "No such product exists on this store!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this@TransactionActivity,
                                "product exists on this store!",
                                Toast.LENGTH_LONG
                            ).show()

                            // hashmap for item count and value
                            if(hashMap.get(barcode) == null){
                                hashMap.put(barcode, 1)
                            }
                            else{
                                var itemCount = hashMap.getValue(barcode) + 1
                                hashMap.put(barcode, itemCount)
                            }

                            // profuct information
                            var productDetail : ArrayList<String> = ArrayList()
                            productDetail.add(products.products.get(0).Description)
                            productDetail.add(products.products.get(0).Price.toString())
                            productDetail.add(products.products.get(0).ProductID.toString())
                            productDetail.add(products.products.get(0).Quantity.toString())
                            productHashmap.put(barcode, productDetail)

                            //Hiding display of Scan products
                            noProductScaned_textView.visibility = View.INVISIBLE
                            // productCodeList.add(barcode)
                            transactionAdapter = TransactionRecyclerAdapter(this@TransactionActivity, productHashmap, hashMap)

                            productTransaction_recycler.adapter = transactionAdapter

                            //Displaying total price layout
                            totalTransactionPriceLinearLayout.visibility = View.VISIBLE
                        }
                    }catch (e: Exception){
                        Log.e("Product Adding Error", "$e")
                    }
                }
            }
        })
    }


    fun fetchCustomerID(){

        var customerUsername = transactionCustomer_ID.text.toString()


        var URL = "http://$ipAddress:8000/customer/$customerUsername"


        if(customerUsername.isEmpty()){

            transactionCustomer_ID.setError("Please Enter Customer Username!")

        }else{


            var request = Request.Builder().url(URL).build()



            var client = OkHttpClient()


            client.newCall(request).enqueue(object : Callback{


                override fun onFailure(call: Call, e: IOException) {

                    runOnUiThread {

                        Toast.makeText(this@TransactionActivity, "Error in fetching customer for transaction!", Toast.LENGTH_SHORT).show()

                    }

                }

                override fun onResponse(call: Call, response: Response) {

                    var body = response.body()?.string()


                   runOnUiThread {

                       try {
                           if (body.equals("[]")) {

                               customerID = 0
                           } else {

                               //var gson = GsonBuilder().create()

                               var customerArray = JSONArray(body)
                               var customerObject = customerArray.getJSONObject(0)

                               var customer_ID = customerObject.getInt("CustomerID")
                               var mobileNo = customerObject.getString("Mobile")
                               var customername = customerObject.getString("Name")

                               customerName = customername
                               customerMobile = mobileNo
                               customerID = customer_ID
                               Log.e("Customer ID", "$customerID")


                           }
                       }catch (e: java.lang.Exception){
                           Log.e("Customer", "$e")
                       }



                   }




                }


            })




        }



    }

    fun addTransaction(saledProducts: String){

        var URL = "http://$ipAddress:8000/post_transaction"

        var jsonType = MediaType.get("application/json; charset=utf-8")

        var client = OkHttpClient()

        var requestBody = RequestBody.create(jsonType, saledProducts)

        var request = Request.Builder().url(URL).post(requestBody).build()

        client.newCall(request).enqueue(object : Callback{


            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {

                    Toast.makeText(this@TransactionActivity, "Failed to Post Transaction ${e.message}", Toast.LENGTH_SHORT).show()

                }


            }

            override fun onResponse(call: Call, response: Response) {


                runOnUiThread {

                    if(response.message() == "OK"){

                        Toast.makeText(this@TransactionActivity, "Transaction Posted!", Toast.LENGTH_SHORT).show()

                    }else{
                        Toast.makeText(this@TransactionActivity, "${response.message()}", Toast.LENGTH_SHORT).show()
                    }

                }




            }


        })










    }

    fun checkStoreInventory(){

        var URL = "http://$ipAddress:8000/storesinventory/$storeID"

        var client = OkHttpClient()


        var request = Request.Builder().url(URL).build()


        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {

                    Toast.makeText(this@TransactionActivity, "Error in Fetching Inventory $e", Toast.LENGTH_LONG).show()

                }

            }

            override fun onResponse(call: Call, response: Response) {


                var body = response.body()?.string()


                 var gson = GsonBuilder().create()

                var products = gson.fromJson(body, StoresProducts::class.java)

                 Log.e("Store Product Indicies", "${body}")

                runOnUiThread {


                    try{

                        var updateDone = false
                       // Log.e("Store Product Indicies", "${products.products.count()}")
                    for ((key, saledProductList) in transactionProductMap) {

                        for (product in products.products) {

                            //var product = products.products.get(i)

                            if (key == product.ProductID) {


                                var quantityleft = 0
                                var remainingQuantity = product.Quantity - saledProductList[1].toInt()

                                if(remainingQuantity <= 0){
                                    quantityleft = 0
                                }else{
                                    quantityleft = remainingQuantity
                                }

                                var updateInentoryJSON = "{\"ProductID\" : \"${product.ProductID}\"," +
                                        "\"StoreID\" : \"$storeID\"," +
                                        "\"RetailerID\" : \"$retailerID\"," +
                                        "\"Quantity\" : \"$quantityleft\" }"
                                updateStoreInventory(updateInentoryJSON)

                            }


                        }

                        updateDone = true

                    }

                        if (updateDone){
                            transactionProductMap.clear()
                        }else{
                            Toast.makeText(this@TransactionActivity, "Please Delete transactionproductmap!", Toast.LENGTH_LONG).show()
                        }

                }catch (e: java.lang.Exception){
                        Log.e("Inven Update Exception", "$e")
                    }




                }

            }


        })



    }

    fun updateStoreInventory(updatedInventory: String){

        var URL = "http://$ipAddress:8000/update_inventory"


        var jsonType = MediaType.get("application/json; charset=utf-8")


        var client = OkHttpClient()

        var requestBody = RequestBody.create(jsonType, updatedInventory)

        var request = Request.Builder().url(URL).put(requestBody).build()



        client.newCall(request).enqueue(object : Callback{


            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {


                    Toast.makeText(this@TransactionActivity, "Error in updating inventory $e", Toast.LENGTH_LONG).show()

                }


            }

            override fun onResponse(call: Call, response: Response) {

                runOnUiThread {

                    if(response.message() == "OK"){


                        Toast.makeText(this@TransactionActivity, "Inventory Updated", Toast.LENGTH_LONG).show()

                    }else{
//                        Toast.makeText(this@TransactionActivity, "Updating inventory ${response.body()}", Toast.LENGTH_LONG).show()

                    Log.e("Updating Inventory", " ${response.body()?.string()}")
                    }



                }



            }


        })



        Log.e("Updated JSON", "$updatedInventory")


    }




}


