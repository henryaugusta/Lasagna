package com.bangkit.nadira.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils.loadAnimation
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bangkit.nadira.R
import com.bangkit.nadira.databinding.ActivityMainBinding
import com.bangkit.nadira.databinding.ActivityMainNewBinding
import com.bangkit.nadira.databinding.LayoutLoginUserBinding
import com.bangkit.nadira.databinding.LayoutRegisterUserBinding
import com.bangkit.nadira.util.Resource
import com.bangkit.nadira.util.SharedPreference.Preference
import com.bangkit.nadira.util.SharedPreference.const.IS_USER_LOGIN
import com.bangkit.nadira.util.SharedPreference.const.USER_EMAIL
import com.bangkit.nadira.util.SharedPreference.const.USER_ID
import com.bangkit.nadira.util.SharedPreference.const.USER_NAME
import com.bangkit.nadira.util.SharedPreference.const.USER_TYPE
import com.bangkit.nadira.util.baseclass.BaseActivity
import com.bangkit.nadira.view.ui.AdminHomeActivity
import com.bangkit.nadira.viewmodel.LoginViewModel
import com.bangkit.nadira.viewmodel.RegisterViewModel
import kotlinx.coroutines.*
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.fixedRateTimer


class MainActivity : BaseActivity() {

    val registerViewModel by lazy { ViewModelProvider(this).get(RegisterViewModel::class.java) }
    val loginViewModel by lazy { ViewModelProvider(this).get(LoginViewModel::class.java) }

    lateinit var d: Date
    lateinit var meTime: String

    lateinit var viewBinding: ActivityMainNewBinding
    lateinit var layoutLogin: LayoutLoginUserBinding
    lateinit var layoutRegister: LayoutRegisterUserBinding

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        viewBinding = ActivityMainNewBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        checkIsLogin()
        setUpBinding()
        setUpClock()

        viewBinding.sampleImage.invalidate()
        viewBinding.sampleImage.drawable.toBitmap(200,400)

        val icon = BitmapFactory.decodeResource(
            resources,
            R.drawable.example_banjir
        )

        classifyImage(icon)



        //Register View Model Listener
        registerViewModel.registerStatus.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    Timber.d("register: loading")
                    "Loading".showLongToast()
                }
                is Resource.Error -> {
                    showSweetAlert("Registrasi Gagal", "Silakan Coba Kembali Nanti", R.color.xdRed)
                    Timber.d("register: error")
                    "Error".showLongToast()
                }
                is Resource.Success -> {
                    showSweetAlert("Registrasi Berhasil", "Registrasi Berhasil", R.color.xdGreen)
                    "Login Berhasil".showLongToast()
                    Timber.d("register: success")
                }
            }
        })

        //Login View Model Listener
        loginViewModel.loginStatus.observe(this, Observer {
            when (it) {
                is Resource.Loading -> {
                    Timber.d("login: loading")
                    "Loading".showLongToast()
                }
                is Resource.Error -> {
                    showSweetAlert("Login Gagal", it.message.toString(), R.color.xdRed)
                    Timber.d("login: error")
                    "Error".showLongToast()
                }
                is Resource.Success -> {
                    showSweetAlert("Login Berhasil", "Selamat Datang $USER_NAME", R.color.xdGreen)
                    //Save User ID , Name and Email to SharedPref

                    if (loginViewModel.USER_TYPE.value == "admin") {
                        Preference(this).apply {
                            save(USER_TYPE, "admin")
                            save(IS_USER_LOGIN, true)
                            save(USER_NAME, loginViewModel.USER_NAME.value.toString())
                            save(USER_ID, loginViewModel.USER_ID.value.toString())
                            save(USER_EMAIL, loginViewModel.USER_EMAIL.value.toString())
                        }
                    } else {
                        Preference(this).apply {
                            save(USER_TYPE, "user")
                            save(IS_USER_LOGIN, true)
                            save(USER_NAME, loginViewModel.USER_EMAIL.value.toString())
                            save(USER_ID, loginViewModel.USER_ID.value.toString())
                            save(USER_EMAIL, loginViewModel.USER_EMAIL.value.toString())
                        }
                        "Login Berhasil".showLongToast()
                        Timber.d("login: success")
                        Timber.d("login: ${loginViewModel.USER_NAME.value}")
                        Timber.d("login: ${loginViewModel.USER_USERNAME.value}")
                    }
                    checkIsLogin()

                }
            }
        })

        //Login Button Logic
        viewBinding.lytLoginUser.let {
            it.btnLogin.setOnClickListener { _ ->
                var isError = false

                val username = it.etUsername.text.toString()
                val password = it.etPassword.text.toString()

                if (it.etUsername.text.isNullOrBlank()) {
                    isError = true
                }
                if (it.etPassword.text.isNullOrBlank()) {
                    isError = true
                }
                if (!isError) {
                    loginViewModel.sendLoginData(username, password)
                } else {
                    "Mohon Lengkapi Semua Kolom Isian Terlebih Dahulu".showLongToast()
                }
            }
        }

        //Register Button Logic
        viewBinding.lytRegisterUser.let {
            it.btnRegister.setOnClickListener { btnReg ->
                val name = it.etNama.text.toString()
                val email = it.etEmail.text.toString()
                val username = it.etUsername.text.toString()
                val contact = it.etPhone.text.toString()
                val password = it.etPassword.text.toString()
                var isError = false
                var gender = ""

                val rgGender = it.rgGender.checkedRadioButtonId.toString()
                gender = if (rgGender == "rb_male") {
                    "1"
                } else {
                    "2"
                }

                if (rgGender.isBlank()) {
                    isError = true
                }
                if (it.etNama.text.isNullOrBlank()) {
                    isError = true
                }
                if (it.etEmail.text.isNullOrBlank()) {
                    isError = true
                }
                if (it.etUsername.text.isNullOrBlank()) {
                    isError = true
                }
                if (it.etPhone.text.isNullOrBlank()) {
                    isError = true
                }
                if (it.etPassword.text.isNullOrBlank()) {
                    isError = true
                }

                if (!isError) {
                    registerViewModel.sendRegisterData(
                        nama = name,
                        username = username,
                        email = email,
                        password = password,
                        jk = gender,
                        no_telp = contact
                    )
                } else {
                    "Mohon Lengkapi Semua Kolom Isian Terlebih Dahulu".showLongToast()
                }
            }
        }

    }

    private fun checkIsLogin() {
        val isLogin: Boolean = Preference(this).getPrefBool(IS_USER_LOGIN) ?: false
        if (isLogin) {
            if (Preference(this).getPrefString(USER_TYPE).toString() == "admin") {
                startActivity(Intent(this, AdminHomeActivity::class.java))
            } else {
                startActivity(Intent(this, MainMenuUserActivity::class.java))
            }
            finish()
        } else {
            //Do Nothing
        }
    }

    private fun setUpClock() {

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
        viewBinding.labelTitle.animation = loadAnimation(this, R.anim.blink)


    }

    private fun setUpBinding() {
        layoutLogin = viewBinding.lytLoginUser
        layoutRegister = viewBinding.lytRegisterUser

        viewBinding.btnInitLogin.setOnClickListener {
            viewBinding.containerInitLogin.visibility = View.GONE
            layoutLogin.parent.visibility = View.VISIBLE
            layoutLogin.parent.animation = loadAnimation(this, R.anim.bottom_appear)
        }

        viewBinding.btnInitRegister.setOnClickListener {
            viewBinding.containerInitLogin.visibility = View.GONE
            layoutRegister.parent.visibility = View.VISIBLE
            layoutRegister.parent.animation = loadAnimation(this, R.anim.bottom_appear)
        }

        viewBinding.lytRegisterUser.btnCloseLoginStudent.setOnClickListener {
            layoutRegister.parent.visibility = View.GONE
            layoutRegister.parent.animation = loadAnimation(this, R.anim.bottom_gone)
            GlobalScope.launch {
                delay(1000)
                withContext(Dispatchers.Main) {
                    viewBinding.containerInitLogin.visibility = View.VISIBLE
                }
            }

        }

        viewBinding.lytLoginUser.btnCloseLoginStudent.setOnClickListener {
            layoutLogin.parent.visibility = View.GONE
            layoutLogin.parent.animation = loadAnimation(this, R.anim.bottom_gone)

            GlobalScope.launch {
                delay(1000)
                withContext(Dispatchers.Main) {
                    viewBinding.containerInitLogin.visibility = View.VISIBLE
                }
            }
        }

        layoutLogin.btnLogin.setOnClickListener {
            startActivity(Intent(this, MainMenuUserActivity::class.java))
        }

    }

    fun classifyImage(image: Bitmap) {

    }

}