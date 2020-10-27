package com.example.sherazali.idpos_app.Home

import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.sherazali.idpos_app.Framents.ReportsViewPager
import com.example.sherazali.idpos_app.R
import kotlinx.android.synthetic.main.activity_reports.*
import lecho.lib.hellocharts.model.PieChartData
import lecho.lib.hellocharts.model.SliceValue

class ReportsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reports)
        //setSupportActionBar(reportsToolbar)



        //Setting PageViewer
        val fragmentsPageAdapter = ReportsViewPager(supportFragmentManager)
        predictions_pageviewer.offscreenPageLimit = 2
        predictions_pageviewer.adapter = fragmentsPageAdapter
        predictions_menu.setupWithViewPager(predictions_pageviewer)


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
}
