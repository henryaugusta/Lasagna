package com.feylabs.lasagna.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.feylabs.lasagna.R
import com.feylabs.lasagna.databinding.ActivityMainBinding
import com.feylabs.lasagna.databinding.LayoutLoginUserBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.fixedRateTimer
import android.view.animation.AnimationUtils.loadAnimation
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    lateinit var d: Date
    lateinit var meTime: String

    lateinit var viewBinding: ActivityMainBinding
    lateinit var layoutLogin: LayoutLoginUserBinding

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        layoutLogin = viewBinding.lytLoginUser


        val sdf = SimpleDateFormat("kk:mm:ss")
        d = Date()
        meTime = sdf.format(d)
        val test4 = DateFormat.getDateInstance(DateFormat.ERA_FIELD).format(Date())


        fixedRateTimer("timer", false, 0, 1000) {
            this@MainActivity.runOnUiThread {
                d = Date()
                meTime = sdf.format(d)
                viewBinding.textB.text = meTime
            }
        }

        viewBinding.textU.text = test4
        
        viewBinding.labelTitle.animation= loadAnimation(this,R.anim.blink)



        viewBinding.btnInitLogin.setOnClickListener {
            viewBinding.containerInitLogin.visibility = View.GONE
            layoutLogin.parent.visibility = View.VISIBLE
            layoutLogin.parent.animation = loadAnimation(this, R.anim.bottom_appear)
        }

        viewBinding.lytLoginUser.btnCloseLoginStudent.setOnClickListener {
            layoutLogin.parent.visibility = View.GONE
            layoutLogin.parent.animation = loadAnimation(this, R.anim.bottom_gone)

            GlobalScope.launch {
                delay(1000)
                withContext(Dispatchers.Main){
                    viewBinding.containerInitLogin.visibility = View.VISIBLE
                }
            }
        }

        layoutLogin.btnLogin.setOnClickListener {
            startActivity(Intent(this,MainMenuUserActivity::class.java))
        }


    }
}