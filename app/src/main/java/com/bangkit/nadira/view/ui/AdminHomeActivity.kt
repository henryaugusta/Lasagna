package com.bangkit.nadira.view.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bangkit.nadira.R
import com.bangkit.nadira.databinding.ActivityAdminHomeBinding
import com.bangkit.nadira.util.SharedPreference.Preference
import com.bangkit.nadira.util.SharedPreference.const.USER_EMAIL
import com.bangkit.nadira.util.SharedPreference.const.USER_NAME
import com.bangkit.nadira.util.baseclass.BaseActivity
import com.bangkit.nadira.view.MainActivity
import com.google.android.material.navigation.NavigationView


class AdminHomeActivity : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    val vbind by lazy { ActivityAdminHomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(vbind.root)
        val toolbar: Toolbar = findViewById(R.id.toolbar_admin)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view_admin)
        val navController = findNavController(R.id.nav_host_fragment)


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_contact,
                R.id.nav_user_management,
                R.id.nav_category
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val labelName = navView.getHeaderView(0).findViewById(R.id.label_title) as TextView
        val labelDesc = navView.getHeaderView(0).findViewById(R.id.label_desc) as TextView

        labelName.text = Preference(this).getPrefString(USER_NAME).toString()
        labelDesc.text = Preference(this).getPrefString(USER_EMAIL).toString()

        val signoutMenuItem = vbind.navViewAdmin.menu.findItem(R.id.btn_logout)
        signoutMenuItem.setOnMenuItemClickListener {
            "Sign Out".showLongToast()
            Preference(this).clearPreferences()
            finish()
            startActivity(Intent(this, MainActivity::class.java))
            true
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_admin, menu)
//        return true
//    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}