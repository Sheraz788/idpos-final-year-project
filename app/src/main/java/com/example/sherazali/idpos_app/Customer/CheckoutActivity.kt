package com.example.sherazali.idpos_app.Customer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.checkout_activity.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class CheckoutActivity : AppCompatActivity() {
    var paymentMethod = ""

    var ipAddress : String? = null// Create a new map
    val cartItemsHashMap = HashMap<Int, ArrayList<String>>()
    var orderid = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.checkout_activity)


        ipAddress = Globals.ipAddress

        //Getting Product Information

        // Get bundle data from intent
        val data = intent.extras



        // Get hash map size
        val size = data!!.getInt("size")

        var totalPrice = 0
        // Get each entry from bundle then put to hash map
        for (i in 0 until size) {
            val key = data.getInt("key$i")
            val value = data.getStringArrayList("value$i")


            totalPrice += (value[1].toInt()) * (value[2].toInt())


            cartItemsHashMap.put(key, value)
        }

        Log.e("Products", "$cartItemsHashMap")
        totalCustomerProductsPrice.text = totalPrice.toString()


        try {
        //Getting Payment Method
        var paymentRadioGroup = findViewById<RadioGroup>(R.id.paymentRadioGroupBtn)

            paymentRadioGroup.setOnCheckedChangeListener { group, checkedId ->

                var getChecked = findViewById<RadioButton>(checkedId)
                var getSelectedText = getChecked.text.toString()

                if (getSelectedText == "Cash") {
                    paymentMethod = "Cash"
                } else if (getSelectedText == "Cash On Delivery") {
                    paymentMethod = "COD"
                }

                Log.e("Payment Method", "$paymentMethod")
            }


        }catch (e:Exception){
            Log.e("Payment Check Exception", "$e")
        }



        start_orderBtn.setOnClickListener {

            checkOrder()
        }



        back_arrow.setOnClickListener {


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


            var cartIntent = Intent(this, CartActivity::class.java)
            cartIntent.putExtras(cartItemsData)
            startActivity(cartIntent)
            overridePendingTransition(

                R.anim.trans_left_in,
                R.anim.trans_right_out
            )
            finish()
        }


    }


    override fun onBackPressed() {
        //super.onBackPressed()



    }


    fun PostOrder(orderJSON: String){
        val JSON = MediaType.get("application/json; charset=utf-8")

        var URL = "http://$ipAddress:8000/customer_order"

        var client = OkHttpClient()

        var body = RequestBody.create(JSON, orderJSON)

        Log.e("Order", "ORder")

        var request = Request.Builder().url(URL).post(body).build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {

                    Toast.makeText(this@CheckoutActivity, "Order Not posted", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onResponse(call: Call, response: Response) {

                runOnUiThread {

                    if(response.code() == 200 && response.message() == "OK"){

                        var OrderItemsJSON = ""
                        for((key, values) in cartItemsHashMap){

                            OrderItemsJSON = "{\"UserID\" : \"${values[5]}\"," +
                                    "\"Quantity\" : \"${values[2]}\"," +
                                    "\"Price\" : \"${values[1]}\"," +
                                    "\"Status\" : \"A\"," +
                                    "\"StoreProductID\" : \"$key\"," +
                                    "\"StoresID\" : \"${values[3]}\"," +
                                    "\"StoresRetailerID\" : \"${values[4]}\"," +
                                    "\"OrderID\" : \"$orderid\" }"
                            PostOrderItems(OrderItemsJSON)
                        }
                        Toast.makeText(this@CheckoutActivity, "Order  Posted", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@CheckoutActivity, "Order Not Posted", Toast.LENGTH_SHORT).show()
                    }


                }
            }


        })




    }


    fun checkOrder(){

        var URL = "http://$ipAddress:8000/order"

        var client = OkHttpClient()


        var request = Request.Builder().url(URL).build()


        client.newCall(request).enqueue(object : Callback{

            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {

                var body = response.body()?.string()
//                Log.e("Order Body", "$body")
                var gson = GsonBuilder().create()

                var orders = gson.fromJson(body, Orders::class.java)

                runOnUiThread {



                    if(orders.orders.isEmpty()){


                        var OrderJSON = ""

                            for((key, values) in cartItemsHashMap){

                                OrderJSON = "{\"OrderID\" : \"$orderid\"," +
                                        "\"CustomerID\" : \"${values[6]}\"," +
                                        "\"StoreID\" : \"${values[3]}\"," +
                                        "\"RetailerID\" : \"${values[4]}\"," +
                                        "\"UserID\" : \"${values[5]}\"," +
                                        "\"PaymentMethod\" : \"$paymentMethod\"," +
                                        "\"Status\" : \"A\" }"
                            }

                            PostOrder(OrderJSON)
                    }else{
                        var orderExists = false
                        for(orderID in orders.orders){
                            if(orderID.OrderID >= orderid){
                                orderid = orderID.OrderID + 1
                                orderExists = true
                            }else{
                                orderExists = false
                            }

                        }

                        if(orderExists){
                            var OrderJSON = ""

                            for((key, values) in cartItemsHashMap){
                                OrderJSON = "{\"OrderID\" : \"$orderid\"," +
                                        "\"CustomerID\" : \"${values[6]}\"," +
                                        "\"StoreID\" : \"${values[3]}\"," +
                                        "\"RetailerID\" : \"${values[4]}\"," +
                                        "\"UserID\" : \"${values[5]}\"," +
                                        "\"PaymentMethod\" : \"$paymentMethod\"," +
                                        "\"Status\" : \"A\" }"
                            }

                            PostOrder(OrderJSON)

                        }


                    }

                }


            }


        })



    }


    fun PostOrderItems(orderItems : String){

        val JSON = MediaType.get("application/json; charset=utf-8")

        var URL = "http://$ipAddress:8000/create_orderItems"

        var client = OkHttpClient()

        var body = RequestBody.create(JSON, orderItems)

        var request = Request.Builder().url(URL).post(body).build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {

                    Toast.makeText(this@CheckoutActivity, "Order Items Not posted", Toast.LENGTH_SHORT).show()
                }

            }

            override fun onResponse(call: Call, response: Response) {

                runOnUiThread {

                    if(response.code() == 200 && response.message() == "OK"){
                        var customerHome = Intent(this@CheckoutActivity, CustomerStoreHomeActivity::class.java)
                        startActivity(customerHome)
                        overridePendingTransition(

                            R.anim.trans_right_in,
                            R.anim.trans_left_out
                        )

                        Toast.makeText(this@CheckoutActivity, "You will be informed soon!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this@CheckoutActivity, "Order Items Not Posted", Toast.LENGTH_SHORT).show()
                    }


                }
            }


        })






    }


}

class Orders (val orders: List<OrderID>)

class OrderID (val OrderID : Int)
