package com.example.sherazali.idpos_app.Customer

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.example.sherazali.idpos_app.Adapters.StoreProductHolder
import com.example.sherazali.idpos_app.Adapters.StoreProductsAdapter
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.Home.ReportsActivity
import com.example.sherazali.idpos_app.R
import com.example.sherazali.idpos_app.Retailer.Categories
import com.example.sherazali.idpos_app.Retailer.Stores
import com.example.sherazali.idpos_app.Retailer.StoresProducts
import com.example.sherazali.idpos_app.Users.LoginActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_customer_store_home.*
import kotlinx.android.synthetic.main.app_bar_customer_store_home.*
import kotlinx.android.synthetic.main.content_customer_store_home.*
import kotlinx.android.synthetic.main.nav_header_customer_store_home.view.*
import okhttp3.*
import org.json.JSONArray
import java.io.IOException

class CustomerStoreHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, StoreProductHolder.cartInterface {

    var products:MutableList<String> = ArrayList()
    var displayList:MutableList<String> = ArrayList()

    var ipAddress : String? = null
    var storeID: Int? = null
    var retailerID: Int? = null
    var customerUSERID : String? = null
    var customerID : Int? = null

    //Cart
    var cartItemsCount = 0
    var countProducts = 0
    var cartItemsHashMap: HashMap<Int, ArrayList<String>> = HashMap()
    var productAddToCart = ArrayList<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_store_home)
        setSupportActionBar(toolbar)


        //Setting up header ...
        var header = LayoutInflater.from(this).inflate(R.layout.nav_header_customer_store_home, null)

        header.minimumHeight = 200
        customer_nav_view.addHeaderView(header)

        //Setting ipAddress
        var globals = Globals

        ipAddress = globals.ipAddress

        customerUSERID = globals.returnUserID()

        fetchCustomer(customerUSERID)

        Log.e("user ID", "${globals.returnUserID()}")

        customerStoreProducts_recyclerView.layoutManager = LinearLayoutManager(this)



        fetchStores()
        if(savedInstanceState == null){
            customer_nav_view.setCheckedItem(R.id.nav_purchases)
        }

        btn_cart.setOnClickListener {

            var cartIntent = Intent(this, CartActivity::class.java)
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

            cartIntent.putExtras(cartItemsData)
            startActivity(cartIntent)

        }


        //Setting Customer Profile

        var customerProfile = Globals.chooseShopDetails

        header.loginCustomer_txt.text = customerProfile.userName


        if(customerProfile.userType == "C"){


            header.loginCustomer_Type.text = "Customer"
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        customer_nav_view.setNavigationItemSelectedListener(this)
    }

    override fun cartItems(productID: Int, productName: String, productExistingQuantity: Int, productPrice: Int) {

        productAddToCart.add(productID)
        var productAdd = 0
        for(productAdded in productAddToCart){

            if(productAdded == productID && productAdd <= productExistingQuantity){

                productAdd++


            }

        }

        if(cartItemsHashMap.isEmpty()){

            cartItemsCount++
            var productDetail = ArrayList<String>()
            productDetail.add(productName)
            productDetail.add(productPrice.toString())
            productDetail.add(productAdd.toString())
            productDetail.add(storeID.toString())
            productDetail.add(retailerID.toString())
            productDetail.add(customerUSERID.toString())
            productDetail.add(customerID.toString())

            cartItemsHashMap.put(productID, productDetail)
            cartCount.text = cartItemsCount.toString()

            //Log.e("Total Products Quantity","$productAdd")

        }else{

            if(productAdd <= productExistingQuantity){
               // Log.e("Total Products Quantity","$productAdd")
                cartItemsCount++
                var productDetail = ArrayList<String>()
                productDetail.add(productName)
                productDetail.add(productPrice.toString())
                productDetail.add(productAdd.toString())
                productDetail.add(storeID.toString())
                productDetail.add(retailerID.toString())
                productDetail.add(customerUSERID.toString())
                productDetail.add(customerID.toString())


                cartItemsHashMap.put(productID, productDetail)
                cartCount.text = cartItemsCount.toString()

            }else{

                Toast.makeText(this, "Only $productExistingQuantity are available!", Toast.LENGTH_LONG).show()
            }


        }
        //Log.e("Cart Items ", "$itemsCount")


        //Toast.makeText(this,"$storeID $productID $productName", Toast.LENGTH_SHORT).show()


    }
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        customer_nav_view.setCheckedItem(R.id.nav_purchases)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.search,menu)
        val searchItem = menu.findItem(R.id.menu_search)
        if(searchItem != null){
            val searchView = searchItem.actionView as SearchView
            val editext = searchView.findViewById<EditText>(android.support.v7.appcompat.R.id.search_src_text)
            editext.hint = "Search Products..."

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {


                    displayList.clear()
                    if(newText!!.isNotEmpty()){
                        val search = newText.toLowerCase()
                        products.forEach {
                            if(it.toLowerCase().contains(search)){
                                displayList.add(it)
                            }
                        }
                    }else{
                        displayList.addAll(products)
                    }
                    customerStoreProducts_recyclerView.adapter?.notifyDataSetChanged()
                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_purchases -> {

            }
//            R.id.nav_order_history -> {
//
//            }
//            R.id.nav_help -> {
//
//            }
//            R.id.nav_setting_customer -> {
//
//            }
//            R.id.nav_report -> {
//
//                val reports_intent = Intent(this, ReportsActivity::class.java)
//                startActivity(reports_intent)
//
//                finish()
//            }
            R.id.nav_customer_logout -> {

                var loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun fetchStores(){

        var URL = "http://$ipAddress:8000/store"

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

                    var storeArrayAdapter = ArrayAdapter<String>(this@CustomerStoreHomeActivity, android.R.layout.simple_spinner_dropdown_item, storesList)


                    chooseStoreCustomer_spinner.adapter = storeArrayAdapter


                    chooseStoreCustomer_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {


                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {



                            if(storesMap.values.toList()[position].equals("No store for this retailer!")){
                                Toast.makeText(this@CustomerStoreHomeActivity, "Add Store then products!", Toast.LENGTH_SHORT).show()


                            }else
                            {

                                storeID = storesMap.keys.toList()[position]

                                fetchRetailersData(storeID!!)
                                fetchStoresProducts()

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

    fun fetchStoresProducts(){

        var URL = "http://$ipAddress:8000/storesproducts/$storeID"

        var client = OkHttpClient()


        var request = Request.Builder().url(URL).build()


        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {

                    Toast.makeText(this@CustomerStoreHomeActivity, "Error in Fetching Products $e", Toast.LENGTH_LONG).show()

                }

            }

            override fun onResponse(call: Call, response: Response) {

                try {
                    var body = response.body()?.string()


//                    Log.e("Stores Products ", "$body")
                    var gson = GsonBuilder().create()

                    var products = gson.fromJson(body, Categories::class.java)

                    runOnUiThread {

                        try {

                            var customerStoreAdapter = StoreProductsAdapter(products)
                            customerStoreProducts_recyclerView.adapter = customerStoreAdapter

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

    fun fetchCustomer(customerUserID: String?){

        var URL = "http://$ipAddress:8000/customer/$customerUserID"

        var client = OkHttpClient()

        var request = Request.Builder().url(URL).build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {


            }

            override fun onResponse(call: Call, response: Response) {

                var body = response.body()?.string()

                runOnUiThread {

                    var customerJSONArray = JSONArray(body)
                    var customerObject = customerJSONArray.getJSONObject(0)

                    customerID = customerObject.getInt("CustomerID")

                }

            }


        })


    }

}




