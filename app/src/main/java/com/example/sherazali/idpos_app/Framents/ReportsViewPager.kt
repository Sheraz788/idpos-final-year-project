package com.example.sherazali.idpos_app.Framents

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class ReportsViewPager (fm: FragmentManager) : FragmentPagerAdapter(fm){


    override fun getItem(position: Int): Fragment? {

        when(position){
            0 -> return Trends()
            1 -> return Suggestions()
            else -> {
                return null
            }
        }

    }

    override fun getCount(): Int {

        return 2

    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> "Trends"
            1 -> "Suggestions"
            else -> {
                return null
            }
        }
    }

}