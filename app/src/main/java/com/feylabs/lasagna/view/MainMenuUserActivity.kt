package com.feylabs.lasagna.view

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.feylabs.lasagna.R
import com.feylabs.lasagna.databinding.ActivityMainMenuUserBinding
import com.feylabs.lasagna.util.baseclass.BaseActivity
import com.feylabs.lasagna.util.SharedPreference.Preference
import com.feylabs.lasagna.view.ui.send_report.UserInputReportActivity
import com.feylabs.lasagna.viewmodel.MainMenuUserViewModel
import com.feylabs.lasagna.viewmodel.ProfileViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainMenuUserActivity : BaseActivity() {

    val vbind by lazy { ActivityMainMenuUserBinding.inflate(layoutInflater) }
    val menuViewModel by lazy { ViewModelProvider(this).get(MainMenuUserViewModel::class.java) }
    val profileViewModel by lazy { ViewModelProvider(this).get(ProfileViewModel::class.java) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vbind.root)

        vbind.myCustomToolbar.inflateMenu(R.menu.top_bar_menu)


        setUpNavController()
        setUpObserver()


        vbind.btnAddReport.setOnClickListener {
            startActivity(Intent(this, UserInputReportActivity::class.java))
        }
    }


    private fun setUpNavController() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_my_report,
                R.id.navigation_settings,
                R.id.navigation_news
            )
        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val menuInflater = menuInflater
//        menuInflater.inflate(R.menu.top_bar_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }



    private fun setUpObserver() {
        menuViewModel.title.observe(this, Observer {
            vbind.myCustomToolbar.title = it
        })


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_logout -> {
                Preference(this).clearPreferences()
                startActivity(Intent(this, MainActivity::class.java))
                "Logout Berhasil".showLongToast()
                finish()
            }
        }
        return super.onOptionsItemSelected(item)

    }
}