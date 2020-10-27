package com.example.sherazali.idpos_app.Retailer

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import com.example.sherazali.idpos_app.Adapters.ProductRecyclerAdapter
import com.example.sherazali.idpos_app.Adapters.ProductsCustomHolder
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.Home.HomeActivity
import com.example.sherazali.idpos_app.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_products.*
import kotlinx.android.synthetic.main.productpopup.*
import kotlinx.android.synthetic.main.productrecycleradapter.*
import kotlinx.android.synthetic.main.productrecycleradapter.view.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class ProductsActivity : AppCompatActivity(), ProductRecyclerAdapter.AdapterCallBack {

    var ipAddress = ""
    var retailerUserID : String? = null


    //Storing New Products Details
    var productIDList = ArrayList<Int>()
    var productQuantity = HashMap<Int, Int>()
    var productPrice = HashMap<Int, Int>()
    var newproduct = false


    var productSelectedAlert = false

    var retailerID : Int? = null
    var storeID : Int? = null

    //Storing Existing Products Details
    var productExistingIDList = ArrayList<Int>()
    var productExistingQuantity = HashMap<Int, Int>()
    var productExistingPrice = HashMap<Int, Int>()
    var productExists = false
    var productExitOnStore = false

    lateinit var productDialog : Dialog
    lateinit var progressbar_loading : Dialog

    lateinit var productAdapter : ProductRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        productDialog = Dialog(this)
        progressbar_loading = Dialog(this)

        var global = Globals
        ipAddress = Globals.ipAddress
        try {
            retailerUserID = Globals.returnUserID()
            Log.e("Retailer ID ", "$retailerUserID")
        }catch (e: Exception){
            Log.e("Retailer ID ", "$e")
        }

        back_arrow.setOnClickListener {

            var homeIntent = Intent(this, HomeActivity::class.java)
            startActivity(homeIntent)
            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out)

            finish()

        }


        addProductsToStore_Btn.setOnClickListener {
            progressbar_loading.setContentView(R.layout.progressbar_loading)

            try{

                if(productExitOnStore && newproduct){

                    Log.e("Existing", "$newproduct $productExitOnStore")

                    var progressbar_TextView =
                        progressbar_loading.findViewById(R.id.progressBar_txtView) as TextView

                    progressbar_TextView.text = "Updating products ... "

                    progressbar_loading.setCancelable(false)
                    progressbar_loading.setCanceledOnTouchOutside(false)
                    progressbar_loading.show()

                    productExistingIDList.forEachIndexed { index, element ->

                        var storesProductJSON = "{\"ProductID\" : \"$element\"," +
                                "\"StoreID\" : \"$storeID\"," +
                                "\"RetailerID\" : \"$retailerID\"," +
                                "\"Quantity\" : \"${productExistingQuantity.values.toList()[index]}\"," +
                                "\"Price\" : \"${productExistingPrice.values.toList()[index]}\"}"


                        Log.e("OLD PRODUCT JSON", "$storesProductJSON")
                        // Handler().postDelayed({
                        updateStoreInventory(storesProductJSON)

                        //  }, 2000)


                    }

                    productIDList.forEachIndexed { index, element ->

                        var storesProductJSON = "{\"ProductID\" : \"$element\"," +
                                "\"StoreID\" : \"$storeID\"," +
                                "\"RetailerID\" : \"$retailerID\"," +
                                "\"Quantity\" : \"${productQuantity.values.toList()[index]}\"," +
                                "\"Price\" : \"${productPrice.values.toList()[index]}\"}"

                        Log.e("NEW PRODUCT JSON", "$storesProductJSON")

//                        var progressbar_TextView =
//                            progressbar_loading.findViewById(R.id.progressBar_txtView) as TextView
//
//                        progressbar_TextView.text = "Adding products ... "
//
//                        progressbar_loading.setCancelable(false)
//                        progressbar_loading.setCanceledOnTouchOutside(false)
//                        progressbar_loading.show()
                        Handler().postDelayed({
                            addProductsToStore(storesProductJSON)
                            progressbar_loading.dismiss()
                        }, 3000)


                    }



                }
                else if(productExitOnStore){
                    productExistingIDList.forEachIndexed { index, element ->

                        var storesProductJSON = "{\"ProductID\" : \"$element\"," +
                                "\"StoreID\" : \"$storeID\"," +
                                "\"RetailerID\" : \"$retailerID\"," +
                                "\"Quantity\" : \"${productExistingQuantity.getValue(element)}\"," +
                                "\"Price\" : \"${productExistingPrice.getValue(element)}\"}"


                        Log.e("ExistingProductsUpdate", "$storesProductJSON")
                        var progressbar_TextView =
                            progressbar_loading.findViewById(R.id.progressBar_txtView) as TextView

                        progressbar_TextView.text = "Updating products ... "

                        progressbar_loading.setCancelable(false)
                        progressbar_loading.setCanceledOnTouchOutside(false)
                        progressbar_loading.show()


                        Handler().postDelayed({
                            updateStoreInventory(storesProductJSON)
                            progressbar_loading.dismiss()

                        }, 2000)


                    }

                }
                else if(newproduct) {
                    productIDList.forEachIndexed { index, element ->

                        var storesProductJSON = "{\"ProductID\" : \"$element\"," +
                                "\"StoreID\" : \"$storeID\"," +
                                "\"RetailerID\" : \"$retailerID\"," +
                                "\"Quantity\" : \"${productQuantity.values.toList()[index]}\"," +
                                "\"Price\" : \"${productPrice.values.toList()[index]}\"}"


                        var progressbar_TextView =
                            progressbar_loading.findViewById(R.id.progressBar_txtView) as TextView

                        progressbar_TextView.text = "Adding products ... "

                        progressbar_loading.setCancelable(false)
                        progressbar_loading.setCanceledOnTouchOutside(false)
                        progressbar_loading.show()
                        Handler().postDelayed({
                            addProductsToStore(storesProductJSON)
                            progressbar_loading.dismiss()
                        }, 3000)


                    }
                }else{
                    Log.e("No matching", "No matching")
                }
            }catch (e: Exception){

                Log.e("Loop Exception", "$e")

            }

        }

        fetchRetailersData()

        products_recyclerView.layoutManager = LinearLayoutManager(this)


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

    override fun onMethodCallBack(value: Boolean, productID : Int, holder: ProductsCustomHolder) {

        if(!value){

            var productAlertDialog = AlertDialog.Builder(this)
            productAlertDialog.setTitle("Delete this product")
            productAlertDialog.setMessage("Are you sure you want to delete?")
            productAlertDialog.setCancelable(false)

            productAlertDialog.setPositiveButton("Yes") { dialog, which ->
                try {

                    var removeList = ArrayList<Int>()

                    productIDList.remove(productID)
                    for (quantity in productQuantity) {
                            if(quantity.key == productID){

                                removeList.add(quantity.key)
                                //removeList.add(price.key)

                            }
                    }

                    for(quantity_price in removeList){

                        productQuantity.remove(quantity_price)
                        productPrice.remove(quantity_price)
                    }

                }catch (e : Exception){

                    Log.e("Product Delete Error", "$e")

                }



            }


            productAlertDialog.setNegativeButton("No"){ dialog, which ->



                var checkedBox = PreferenceManager.getDefaultSharedPreferences(applicationContext).getBoolean("Checked", false)


                holder.view.product_CheckBox.isChecked = checkedBox
                productDialog.cancel()
                Log.e("Values ", "$productQuantity  $productPrice")


            }

            productAlertDialog.create().show()


        }else{

            productPopUp(productID)

        }



    }

    fun productPopUp(productID: Int){

        try {
            productDialog.setContentView(R.layout.productpopup)

            var confrimBtn = productDialog.findViewById(R.id.confirm_productBtn) as Button
            var quantityEditText = productDialog.findViewById(R.id.productQuantity_value) as EditText

            var priceEditText = productDialog.findViewById(R.id.productPrice_value) as EditText

            //checking existing product ID's
            var productExist = checkExistingProducts(productID)


            confrimBtn.setOnClickListener {

                if(quantityEditText.text.toString() != "" && priceEditText.text.toString() != "" ){


                    if(productExists){

                        productExitOnStore = productExists
                        var quantity = quantityEditText.text.toString()
                        var price = priceEditText.text.toString()

                        for(existingProductID in productExistingIDList){

                           if(existingProductID == productID){

                               Log.e("Product Existing IDs","$existingProductID")

                               productExistingQuantity.put(existingProductID, (productExistingQuantity.getValue(existingProductID)+quantity.toInt()))

                               Log.e("ProductExistingQuantity","$productExistingQuantity")
                               // priceEditText.text = productExistingPrice.getValue(existingProductID) as Editable
                               productExistingPrice.put(existingProductID, price.toInt())

                               Log.e("Product Existing Price", "$productExistingPrice")


                           }

                        }

                        productExists = false
                        productDialog.dismiss()



                    }else {

                        var quantity = quantityEditText.text.toString()
                        var price = priceEditText.text.toString()

                        productQuantity.put(productID, quantity.toInt())
                        productPrice.put(productID, price.toInt())

                        productDialog.dismiss()
                    }
                }else{
                    Toast.makeText(this, "Please enter values ..." , Toast.LENGTH_LONG).show()
                }


            }


            //productDialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            productDialog.setCanceledOnTouchOutside(false)

            productDialog.setCancelable(false)
            productDialog.show()



        }catch (e: Exception){

            Log.e("Pop UP ExCeptioN", "$e")

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
                        //Log.e("retailers List", "$retailersList")

                        var retailerArrayAdapter = ArrayAdapter<String>(this@ProductsActivity, android.R.layout.simple_spinner_item, retailersList)
                        retailerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)


                        selectRetailer_spinner.adapter = retailerArrayAdapter

                        selectRetailer_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onNothingSelected(parent: AdapterView<*>?) {

                            }

                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {


//                                if(parent?.getItemAtPosition(position)!!.equals("Select a Retailer!")){
//
//                                }else{
//
//                                }


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


        retailerID = retailerIDs


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


                            storesMap.put(store.StoreID, store.StoreName)

                        }
                    }



                    for (storeNames in storesMap){

                        storesList.add(storeNames.value)

                    }


                    var storeArrayAdapter = ArrayAdapter<String>(this@ProductsActivity, android.R.layout.simple_spinner_dropdown_item, storesList)


                    selectStore_spinner.adapter = storeArrayAdapter


                    selectStore_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {


                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {



                            if(storesMap.values.toList()[position].equals("No store for this retailer!")){
                                Toast.makeText(this@ProductsActivity, "Add Store then products!", Toast.LENGTH_SHORT).show()

                                addProductsToStore_Btn.isClickable = false
                                addProductsToStore_Btn.setBackgroundColor(Color.parseColor("#96cfab"))

                                fetchCategoriesData()
                            }else
                            {

                                storeID = storesMap.keys.toList()[position]

                                addProductsToStore_Btn.isEnabled = true
                                fetchCategoriesData()
                            }




                        }


                    }


                }



            }

        })




    }

    fun fetchCategoriesData(){


        var URL = "http://$ipAddress:8000/categories"

        var client = OkHttpClient()

        var request = Request.Builder().url(URL).build()


        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {

                    Toast.makeText(this@ProductsActivity, "Error in Fetching Categories $e", Toast.LENGTH_LONG).show()

                }


            }

            override fun onResponse(call: Call, response: Response) {

                var body = response.body()?.string()



                var gson = GsonBuilder().create()

                var categories = gson.fromJson(body, Categories::class.java)


                runOnUiThread {


                    var categoriesMap = HashMap<Int, String>()

                    for (category in categories.categories){


                        categoriesMap.put(category.CategoryID, category.Description)


                    }



                    var categoriesList : List<String> = ArrayList()


                    for(category in categoriesMap){


                        categoriesList += category.value


                    }


                    var categoriesArrayAdapter = ArrayAdapter<String>(this@ProductsActivity, android.R.layout.simple_spinner_item, categoriesList)

                    categoriesArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    productCategories_spinner.adapter = categoriesArrayAdapter


                    productCategories_spinner.onItemSelectedListener  = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {


                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                            fetchProducts(categoriesMap.keys.toList()[position])

                        }


                    }




                }



            }


        })






    }

    fun fetchProducts(categoryID: Int){


        try {

            var URL = "http://$ipAddress:8000/products/$categoryID"

            var client = OkHttpClient()


            var request = Request.Builder().url(URL).build()


            client.newCall(request).enqueue(object : Callback {

                override fun onFailure(call: Call, e: IOException) {

                    runOnUiThread {

                        Toast.makeText(this@ProductsActivity, "Error in Fetching Products $e", Toast.LENGTH_LONG).show()

                    }

                }

                override fun onResponse(call: Call, response: Response) {


                    var body = response.body()?.string()


                    var gson = GsonBuilder().create()

                    var products = gson.fromJson(body, Products::class.java)

                    runOnUiThread {


                         productAdapter = ProductRecyclerAdapter(products, this@ProductsActivity)
                        products_recyclerView.adapter = productAdapter


                       productIDList = productAdapter.selectedProductList

                       productSelectedAlert = productAdapter.productIsSelected

                    }

                }


            })


        }catch (e: Exception){
            Log.e("Products Exception", "$e")
        }



    }


    //This method will add products to selected store
    fun addProductsToStore(addproductsJSON: String){

        var URL = "http://$ipAddress:8000/addproducts"

        var jsonType = MediaType.get("application/json; charset=utf-8")

        var client = OkHttpClient()

        var bodyRequest = RequestBody.create(jsonType, addproductsJSON)

        var request = Request.Builder().url(URL).post(bodyRequest).build()


        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {
                    Toast.makeText(this@ProductsActivity, "Error in added products to store $e", Toast.LENGTH_LONG).show()
                }

            }

            override fun onResponse(call: Call, response: Response) {

                runOnUiThread {

                    if(response.message() == "OK"){

                        var inventoryIntent = Intent(this@ProductsActivity, InventoryActivity::class.java)
                        startActivity(inventoryIntent)

                        Toast.makeText(this@ProductsActivity, "Products added to Store", Toast.LENGTH_LONG).show()
                    }

                }
            }


        })







    }


    //Check if products already exists on the store
    fun checkExistingProducts(productID: Int) : Boolean{


        var URL = "http://$ipAddress:8000/checkproducts/$productID"

        var client = OkHttpClient()



        var request = Request.Builder().url(URL).build()


        client.newCall(request).enqueue(object : Callback{


            override fun onFailure(call: Call, e: IOException) {



            }

            override fun onResponse(call: Call, response: Response) {


             try{
                var body = response.body()?.string()

                var gson = GsonBuilder().create()


                var productIDs = gson.fromJson(body, ProductsOnStore::class.java)


                runOnUiThread {

                    try {

                        if(productIDs.productIDs.isEmpty()){

                            newproduct = true

                            for (ExistingProductId in productExistingIDList){

                                 productIDList.remove(ExistingProductId)


                            }

                            Log.e("New Product", "$newproduct")
                        }else{
                            for (ExistingProductId in productIDs.productIDs) {


                                if (ExistingProductId.ProductID == productID) {

                                    productExistingIDList.add(productID)

                                    productExistingQuantity.put(ExistingProductId.ProductID, ExistingProductId.Quantity)
                                    productExistingPrice.put(ExistingProductId.ProductID, ExistingProductId.Price)
                                    Log.e("ID Exists", "$productID")
                                    Log.e("Quantity Exists", "$productExistingQuantity")
                                    Log.e("Price Exists", "$productExistingPrice")

                                    productExists = true


                                } else {

                                    productExists = false


                                }

                            }
                        }

                    }catch (e: Exception){

                        Log.e("Error existing product","$e")

                    }

                }

            }catch (e: Exception){
                 Log.e("Checking Existing", "$e")
             }

            }


        })


        return productExists


    }

    //Update Existing product if retailer wants to add more products
    fun updateStoreInventory(updatedInventory: String){

        var URL = "http://$ipAddress:8000/update_storeproducts"


        var jsonType = MediaType.get("application/json; charset=utf-8")


        var client = OkHttpClient()

        var requestBody = RequestBody.create(jsonType, updatedInventory)

        var request = Request.Builder().url(URL).put(requestBody).build()



        client.newCall(request).enqueue(object : Callback{


            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {


                    Toast.makeText(this@ProductsActivity, "Error in updating products $e", Toast.LENGTH_LONG).show()

                }


            }

            override fun onResponse(call: Call, response: Response) {

                runOnUiThread {

                    if(response.message() == "OK"){

                        var inventoryIntent = Intent(this@ProductsActivity, InventoryActivity::class.java)
                        startActivity(inventoryIntent)

                        Toast.makeText(this@ProductsActivity, "Store Product Updated", Toast.LENGTH_LONG).show()

                    }else{
//                        Toast.makeText(this@TransactionActivity, "Updating inventory ${response.body()}", Toast.LENGTH_LONG).show()

                        Log.e("Updating product Error", " ${response.body()?.string()}")
                    }



                }



            }


        })



        Log.e("Updated JSON", "$updatedInventory")


    }

}

class ProductRetailers(val retailers : List<ProductRetailer>)

class ProductRetailer(var RetailerID: Int, var RetailerName: String)

class Categories (val categories: List<Category>)

class Category( val CategoryID: Int, val Description: String, val Product_Name: String, val Price: Int, val Quantity: Int, val ProductID: Int)

class Products(val products : List<Product>)

class Product(val ProductID: Int, val Description: String, val Price : Int, val Quantity : Int)

class ProductsOnStore(val productIDs : List<ProductID>)

class ProductID(val ProductID: Int, val Quantity : Int ,val Price : Int)