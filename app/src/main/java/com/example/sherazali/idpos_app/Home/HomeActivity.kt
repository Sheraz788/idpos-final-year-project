package com.example.sherazali.idpos_app.Home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.R
import com.example.sherazali.idpos_app.Retailer.*
import com.example.sherazali.idpos_app.Salesman.CreateSaleman
import com.example.sherazali.idpos_app.Users.CreateAccount
import com.example.sherazali.idpos_app.Users.LoginActivity
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.android.synthetic.main.nav_header_home.view.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.lang.Exception

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    var userIsLoggedIn = true

    var ipAddress : String? = null
    var storeID : Int? = null
    var retailerID : Int? = null
    var UserID : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
       //overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out)
        setSupportActionBar(toolbar)

        var header : View = LayoutInflater.from(this).inflate(R.layout.nav_header_home, null)
        header.minimumHeight = 200
        nav_view.addHeaderView(header)
//        header.profileImage.clipToOutline = true


        //Getting ipAddress
        var globals = Globals
        ipAddress = globals.ipAddress
        UserID = globals.returnUserID()

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

//        dataSyncProgress.visibility = View.INVISIBLE


        //Setting which user is login to the app
        var profilDetails = Globals

        header.loginUser_txt.text = Globals.userName

        //Hiding menus for retailer and seller based on userType already logged In
//        var gettingUSERPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//        var userType = gettingUSERPreferences.getString("userType", "value")


        try {
            if (Globals.userType == "R") {
                header.loginUser_Type.text = "Retailer"

                hideSalesManMenuItems()

            } else if (Globals.userType == "C" ) {
                header.loginUser_Type.text = "Customer"

            } else if (Globals.userType == "S") {
                header.loginUser_Type.text = "Sales Man"

                hideRetailerMenuItems()

            }
        }catch (e: Exception){
            Log.e("Condition Error", "$e")
        }


        var reportRadioGroup = findViewById<RadioGroup>(R.id.ReportRadioGroup)

        reportRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            var checkedBtn =findViewById<RadioButton>(checkedId)
            var checkedText = checkedBtn.text.toString()

            if(checkedText == "Daily"){

                getReport("day")

            }else if(checkedText == "Weekly"){
                getReport("week")

            }else if(checkedText == "Monthly"){

                getReport("month")

            }


        }



        fetchStores()

        if(savedInstanceState == null){
            nav_view.setCheckedItem(R.id.nav_home)
        }


    }

    //Hidding Menu Items if User is Sales Man
    fun hideRetailerMenuItems(){

        val menu = nav_view.menu
        //menu.findItem(R.id.nav_transactions).isVisible = false
        menu.findItem(R.id.nav_store).isVisible = false
        menu.findItem(R.id.nav_retails).isVisible = false
        menu.findItem(R.id.nav_saleman).isVisible = false
        menu.findItem(R.id.nav_products).isVisible = false

    }

    //Hidding Menu Items if User is Retailer
    fun hideSalesManMenuItems(){

        val menu = nav_view.menu

    }

    override fun onResume() {
        super.onResume()
        nav_view.setCheckedItem(R.id.nav_home)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()

        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        when (item.itemId) {
//            R.id.data_sync -> {
//                dataSyncProgress.visibility = View.VISIBLE
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.


        when (item.itemId) {
            R.id.nav_home -> {

            }
            R.id.nav_retails -> {

                Handler().postDelayed({
                    val retails_intent = Intent(this, RetailsActivity::class.java)
                    startActivity(retails_intent)

                    finish()

                }, 300)
                //overridePendingTransition(R.anim.trans_right_in, R.anim.trans_left_out)

            }



            R.id.nav_store ->{

                Handler().postDelayed({
                    val storeActivityIntent = Intent(this, StoresActivity::class.java)
                    startActivity(storeActivityIntent)

                    finish()

                }, 300)


            }

            R.id.nav_notifications -> {

                Handler().postDelayed({

                    var notificationActivityIntent = Intent(this, NotificationsActivity::class.java)

                    startActivity(notificationActivityIntent)

                    finish()

                }, 300)


            }
            R.id.nav_inventory -> {

                Handler().postDelayed({

                    var inventoryActivityIntent = Intent(this, InventoryActivity::class.java)

                    startActivity(inventoryActivityIntent)

                    finish()

                }, 300)

            }


            R.id.nav_products -> {


                Handler().postDelayed({
                    val productsActivityIntent = Intent(this@HomeActivity, ProductsActivity::class.java)
                    startActivity(productsActivityIntent)


                    finish()

                }, 300)



            }

            R.id.nav_saleman -> {

                Handler().postDelayed({
                    val createSalemanIntent = Intent(this, CreateSaleman::class.java)
                    startActivity(createSalemanIntent)

                    finish()

                }, 300)

            }

            R.id.nav_transactions -> {

                Handler().postDelayed({

                    var transactionIntent = Intent(this, TransactionActivity::class.java)
                    startActivity(transactionIntent)

                    finish()
                }, 300)

            }


            R.id.nav_report -> {

                val reports_intent = Intent(this, ReportsActivity::class.java)
                startActivity(reports_intent)

                finish()
            }
//            R.id.nav_setting -> {
//
//                val setting_intent = Intent(this, SettingActivity::class.java)
//                startActivity(setting_intent)
//                finish()
//            }
            R.id.nav_logout -> {

                var profilDetails = Globals

                profilDetails.userType = ""

                //Removing user on loggedout button clicked


                val logout_intent = Intent(this, LoginActivity::class.java)
                startActivity(logout_intent)
                finish()
                //overridePendingTransition(R.anim.trans_left_in, R.anim.trans_right_out)

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    fun fetchStores(){

        var URL = ""

        if(Globals.userType == "R"){
            URL = "http://$ipAddress:8000/fetch_stores/$UserID"

            Log.e("URL","$URL")
        }else{
            URL = "http://$ipAddress:8000/saleman_stores/$UserID"
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

                    var storeArrayAdapter = ArrayAdapter<String>(this@HomeActivity, android.R.layout.simple_spinner_dropdown_item, storesList)


                    reportsStoreSpinner.adapter = storeArrayAdapter


                    reportsStoreSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {


                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {



                            if(storesMap.values.toList()[position].equals("No store for this retailer!")){
                                Toast.makeText(this@HomeActivity, "Add Store then products!", Toast.LENGTH_SHORT).show()


                            }else
                            {

                                storeID = storesMap.keys.toList()[position]

                                fetchRetailersData(storeID!!)

                                Log.e("Store ID", "$storeID")
                                var reportRadioGroup = findViewById<RadioGroup>(R.id.ReportRadioGroup)
                                var checkedRadioID = reportRadioGroup.checkedRadioButtonId
                                var typeOfReport = findViewById<RadioButton>(checkedRadioID)
                                var checkedText = typeOfReport.text.toString()

                                if(checkedText == "Daily"){

                                    getReport("day")

                                }else if(checkedText == "Weekly"){
                                    getReport("week")

                                }else if(checkedText == "Monthly"){

                                    getReport("month")

                                }

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

    fun getReport(reportType:String){

        var URL = ""

        if(reportType == "day"){
             URL = "http://$ipAddress:8000/day_report/$storeID"
        }else if(reportType == "week"){
            URL = "http://$ipAddress:8000/week_report/$storeID"
        }else if(reportType == "month"){
            URL = "http://$ipAddress:8000/month_report/$storeID"
        }



        var client = OkHttpClient()



        var request = Request.Builder().url(URL).build()


        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {



            }

            override fun onResponse(call: Call, response: Response) {

                var body = response.body()?.string()

//                var gson = GsonBuilder().create()
//
//                var reports = gson.fromJson(body, Reports::class.java)

                Log.e("Report", "$body")

                runOnUiThread {

                    if(body.equals("[]")){
                        Toast.makeText(this@HomeActivity, "No Sales on this store!", Toast.LENGTH_LONG).show()
                        total_customer.text = (0).toString()
                        total_sale.text =(0).toString()
                    }else{
                        var reportJSONArray = JSONArray(body)
                        var reportJSONObject = reportJSONArray.getJSONObject(0)

                        total_customer.text = reportJSONObject.getString("TotalCustomer")
                        total_sale.text = reportJSONObject.getString("TotalSale")
                    }

                }







            }


        })









    }
}

