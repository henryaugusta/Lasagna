package com.feylabs.lasagna.view.ui.admin

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.feylabs.lasagna.R
import com.feylabs.lasagna.databinding.ActivityAdminHomeBinding
import com.feylabs.lasagna.util.SharedPreference.Preference
import com.feylabs.lasagna.util.baseclass.BaseActivity
import com.feylabs.lasagna.view.MainActivity
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
                R.id.nav_user_management
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val signoutMenuItem = vbind.navViewAdmin.menu.findItem(R.id.btn_logout)
        signoutMenuItem.setOnMenuItemClickListener {
            "Sign Out".showLongToast()
            Preference(this).clearPreferences()
            finish()
            startActivity(Intent(this,MainActivity::class.java))
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_admin, menu)
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}