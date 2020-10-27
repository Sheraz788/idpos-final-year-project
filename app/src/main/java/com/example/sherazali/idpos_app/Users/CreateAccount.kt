package com.example.sherazali.idpos_app.Users

import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.example.sherazali.idpos_app.Customer.CreateCustomer
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.Home.HomeActivity
import com.example.sherazali.idpos_app.R
import com.example.sherazali.idpos_app.Retailer.CreateRetailer
import kotlinx.android.synthetic.main.activity_create_account.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class CreateAccount : AppCompatActivity() {

    var b = Bundle()
    var selected_Image : Bitmap? = null
    var ipAddress = ""
    var retailerUSERID = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        //postUser()

        var global = Globals
        ipAddress = Globals.ipAddress




            if(global.userType == "R"){

                retailerUSERID = global.returnUserID()!!

        }else if(global.userType == ""){

        }

        createUser_Btn.setOnClickListener {

            var nameValidation = false
            var emailValidation = false
            var personNameValidation = false
            var passwordValidation = false
            var imageValidation = false




            try {

                //Log.e("JSON", "${returnJSON()}")
                if (username.length() < 6) {

                    username.setError("User ID Atleast 6 words!")
                    nameValidation = false

                } else {
                    nameValidation = true
                }

                if (person_name.length() < 6) {

                    person_name.setError("Person Name Atleast 6 words!")
                    personNameValidation = false

                } else {
                    personNameValidation = true
                }

                if (!isValidEmail(useremail.text.toString())) {

                    useremail.setError("Enter valid email!")
                    emailValidation = false

                } else {
                    emailValidation = true
                }



                if (store_password.length() <= 0) {

                    store_password.setError("Enter password")
                    passwordValidation = false

                } else if (confirm_password.text.toString() != store_password.text.toString()) {

                    confirm_password.setError("Password didn't matched!")
                    passwordValidation = false

                } else {

                    passwordValidation = true

                }

            }catch (e: Exception){
                Log.e("Found Exception", "$e")
            }


            if(nameValidation && emailValidation && personNameValidation && passwordValidation){
                try{

                    //Getting User Type
                    var checkedTypeID = TypeRadioBtn.checkedRadioButtonId
                    var TypeRadioBtn = findViewById<RadioButton>(checkedTypeID)
                    var getTypeValue = TypeRadioBtn.text.toString()

                    if(getTypeValue == "Retailer"){

                        var createRetailerIntent = Intent(this, CreateRetailer::class.java)
                        createRetailerIntent.putExtra("USERINFO", returnUserJSON())
                        createRetailerIntent.putExtra("USERID", username.text.toString())
                        createRetailerIntent.putExtra("USEREMAIL", useremail.text.toString())
                        createRetailerIntent.putExtra("OWNERNAME", person_name.text.toString())
                        startActivity(createRetailerIntent)

                    }else if(getTypeValue == "Customer"){

                        var createCustomerIntent = Intent(this, CreateCustomer::class.java)
                        createCustomerIntent.putExtra("USERINFO", returnUserJSON())
                        createCustomerIntent.putExtra("USERID", username.text.toString())
                        createCustomerIntent.putExtra("USEREMAIL", useremail.text.toString())
                        createCustomerIntent.putExtra("CUSTOMERNAME", person_name.text.toString())
                        startActivity(createCustomerIntent)

                    }


                }catch (e: Exception){
                    Log.e("Exception", "$e")
                }
            }

        }

    }


    override fun onBackPressed() {


       if (Globals.userType == "R"){

           val home_intent = Intent(this, HomeActivity::class.java)
           startActivity(home_intent)
           overridePendingTransition(
               R.anim.trans_left_in,
               R.anim.trans_right_out
           )
           finish()

       }else{

           super.onBackPressed()


       }

    }

    fun isValidEmail(email:String) : Boolean{

        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() && !email.isEmpty()
    }

    //create and return JSON data entered by the user
    fun returnUserJSON() : String{




        var username = username.text.toString()
        var personname = person_name.text.toString()
        var useremail = useremail.text.toString()
        var userpassword = store_password.text.toString()

        //Getting User Type
        var checkedTypeID = TypeRadioBtn.checkedRadioButtonId
        var TypeRadioBtn = findViewById<RadioButton>(checkedTypeID)
        var getTypeValue = TypeRadioBtn.text.toString()





        //Setting Type R, C , S or D
        var Type = ""
        if(getTypeValue == "Retailer"){

            Type = "R"

        }else if(getTypeValue == "Customer"){

            Type = "C"

        }else if(getTypeValue == "Distributor"){

            Type = "D"

        }else if(getTypeValue == "SalesMan"){
            Type = "S"
        }



        return  "{\"UserID\" : \"$username\"," +
                "\"Name\" : \"$personname\"," +
                "\"Email\" : \"$useremail\"," +
                "\"Password\" : \"$userpassword\"," +
                "\"Type\" : \"$Type\"," +
                "\"Status\" : \"A\"}"

    }



}
