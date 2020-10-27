package com.example.sherazali.idpos_app.Home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.sherazali.idpos_app.R
import com.example.sherazali.idpos_app.Users.LoginActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*
import java.lang.Exception

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)





        val background = object : Thread() {

            override fun run() {
                super.run()
                try{

                    val uptodown = AnimationUtils.loadAnimation(this@SplashScreen,
                        R.anim.fadein_animation
                    )
                    val downtoup = AnimationUtils.loadAnimation(this@SplashScreen,
                        R.anim.downtoup
                    )

                    var splashScreen_logo = findViewById(R.id.splashScreen_logo) as ImageView
                    splashScreen_logo.visibility = View.VISIBLE

                    splashScreen_logo.post(object : Runnable{
                        override fun run() {
                            splashScreen_logo.startAnimation(uptodown)
                        }

                    })
//                    splash_linearLayout2.animation = downtoup

                    Thread.sleep(5000)

                    var homeObject = HomeActivity()


                    val intent = Intent(this@SplashScreen, LoginActivity::class.java)
                    startActivity(intent)


                }catch (e : Exception){
                    Log.e("Splash Screen Exception", "$e")
                }

            }
        }

        background.start()

    }
}
