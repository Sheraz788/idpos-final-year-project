package com.example.sherazali.idpos_app

import android.graphics.Bitmap

class Globals {

    companion object chooseShopDetails {

        var choosen_image : Bitmap? = null

        fun returnImage() : Bitmap {

            return  choosen_image!!

        }

        var userName : String? = null

        fun returnUserName() : String?{

            return userName
        }

        var userType : String? = null

        fun returnTypeName() : String?{

            return userType
        }

        var userID : String? = null
            fun returnUserID() : String?{

                return userID

        }

        var productIsSelected : Boolean? = null

        fun productSelected() : Boolean? {

            return productIsSelected

        }

//        var retailerID : Int? = null
//        fun returnretailerID() : Int?{
//
//            return retailerID
//
//
//        }



//        var ipAddress = "192.168.100.6"
//        var ipAddress = "192.168.43.2"
        var ipAddress = "172.20.10.3"
//        var ipAddress = "10.113.51.8"

    }


}