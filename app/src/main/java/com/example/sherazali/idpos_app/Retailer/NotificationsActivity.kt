package com.example.sherazali.idpos_app.Retailer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.example.sherazali.idpos_app.Adapters.NotificationsAdapter
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.Home.HomeActivity
import com.example.sherazali.idpos_app.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_notifications.*
import okhttp3.*
import java.io.IOException

class NotificationsActivity : AppCompatActivity() {


    var ipAddress : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)


        ipAddress = Globals.ipAddress


        notification_recyclerview.layoutManager = LinearLayoutManager(this)

        back_arrow.setOnClickListener {

            val home_intent = Intent(this, HomeActivity::class.java)
            startActivity(home_intent)

            overridePendingTransition(
                R.anim.trans_left_in,
                R.anim.trans_right_out
            )
            finish()


        }

        fetchNotifications()
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


    fun fetchNotifications(){


        var URL = "http://$ipAddress:8000/get_notifications"

        var client = OkHttpClient()


        var request = Request.Builder().url(URL).build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {

                var body = response.body()?.string()

                var gson = GsonBuilder().create()

                var notifications = gson.fromJson(body, Notifications::class.java)


                runOnUiThread {


                    notification_recyclerview.adapter = NotificationsAdapter(notifications)


                }

            }


        })





    }

}

class Notifications (val notifications: List<Notification>)

class Notification(val CustomerName: String, val Contact : String, val StoreName: String, val ProductName: String, val Quantity : Int, val Price : Int)

