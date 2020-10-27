package com.example.sherazali.idpos_app.Users

import android.app.Dialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.sherazali.idpos_app.Customer.CustomerStoreHomeActivity
import com.example.sherazali.idpos_app.Globals
import com.example.sherazali.idpos_app.Home.HomeActivity
import com.example.sherazali.idpos_app.R
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.*
import java.io.IOException
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    var ipAddress = ""

    lateinit var progressbarLoadingDialog : Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Setting Underling for Forget Password
        //forget_password_txt.paintFlags = Paint.UNDERLINE_TEXT_FLAG



        progressbarLoadingDialog = Dialog(this)

        var globals= Globals
        ipAddress = Globals.ipAddress


        login_Btn.setOnClickListener {

            getUsers()


        }

    }


    fun getUsers(){


        val client = OkHttpClient()

        var URL = "http://$ipAddress:8000/users"


        val request = Request.Builder().url(URL).build()

        client.newCall(request).enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {

                runOnUiThread {

                    Toast.makeText(this@LoginActivity, "Fetching User Error: $e", Toast.LENGTH_LONG).show()


                }
            }

            override fun onResponse(call: Call, response: Response) {

                val body = response.body()?.string()


                val gson = GsonBuilder().create()

                val users = gson.fromJson(body, Users::class.java)

                checkUser(users)


            }


        })

    }

    fun checkUser(users: Users){


        try {

            runOnUiThread {
                progressbarLoadingDialog.setContentView(R.layout.progressbar_loading)

                var progressBar_TextView = progressbarLoadingDialog.findViewById(R.id.progressBar_txtView) as TextView

                progressBar_TextView.text = "Signing in ... "


                var useremail = userLogin_email.text.toString()
                var userpassword = userLogin_password.text.toString()

                var userFound = false
                try {
                    for (user in users.users) {


                        if (useremail == user.Email && useremail !=  "" && userpassword == user.Password && userpassword != "") {

                            if (user.Type == "R" || user.Type == "S") {
                                try{

                                    userFound = true


//                                login_progressBar.visibility = View.VISIBLE
                                    progressbarLoadingDialog.setCancelable(false)
                                    progressbarLoadingDialog.setCanceledOnTouchOutside(false)
                                    progressbarLoadingDialog.show()
                                    Handler().postDelayed({


                                        var homeIntent = Intent(this, HomeActivity::class.java)

                                        var global = Globals

                                        Globals.userName = user.Name
                                        Globals.userType = user.Type
                                        Globals.userID = user.UserID

                                        startActivity(homeIntent)

                                        progressbarLoadingDialog.dismiss()
                                    }, 3000)

                                    break
                                }catch (e: Exception){
                                    Log.e("Getting User Error ", "$e")
                                }
                            }else if(user.Type == "C"){
                                try{

                                    userFound = true


                                    progressbarLoadingDialog.setCancelable(false)
                                    progressbarLoadingDialog.setCanceledOnTouchOutside(false)
                                    progressbarLoadingDialog.show()



                                    Handler().postDelayed({

                                        var customerHome = Intent(this, CustomerStoreHomeActivity::class.java)

                                        var globalwa = Globals

                                        Globals.userName = user.Name
                                        Globals.userType = user.Type
                                        Globals.userID = user.UserID

                                        startActivity(customerHome)

                                        progressbarLoadingDialog.dismiss()
                                    }, 3000)
                                    break
                                }catch (e: Exception){
                                    Log.e("Getting User Error ", "$e")
                                }
                            }



                        } else {

                            userFound = false

                        }


                    }
                }catch (e:Exception){
                    Log.e("USER EXCEPTION", "$e")
                }


                if(userFound){
                    loginError_txt.visibility = View.INVISIBLE
                }else{

                    progressbarLoadingDialog.setCancelable(false)
                    progressbarLoadingDialog.setCanceledOnTouchOutside(false)

                    progressbarLoadingDialog.show()
                    Handler().postDelayed({

                        loginError_txt.visibility = View.VISIBLE

                        progressbarLoadingDialog.dismiss()
                    }, 3000)
                }

            }
        }catch (e: Exception){
            Log.e("Exception", "$e")
        }


    }
    fun signUpActivity(view: View){

        var intent_register = Intent(this, CreateAccount::class.java)
        startActivity(intent_register)
    }
}

class Users(val users: List<User>)

class User(val UserID : String, val Name: String, val Email: String, val Password: String, val Type: String, val Status: String)
