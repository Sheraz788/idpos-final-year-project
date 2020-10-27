package com.example.sherazali.idpos_app.Home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.example.sherazali.idpos_app.R
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)


        var settingItems = arrayOf("Data Sync Setting", "Show Notification", "Profile Setting", "Password Setting")

        var setting_adaptor = ArrayAdapter(this, android.R.layout.simple_list_item_1, settingItems)

        settingList.adapter = setting_adaptor


        back_arrow.setOnClickListener {


            val home_intent = Intent(this, HomeActivity::class.java)
            startActivity(home_intent)

            overridePendingTransition(
                R.anim.trans_left_in,
                R.anim.trans_right_out
            )
            finish()

        }

    }
}
