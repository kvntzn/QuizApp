package com.heathkev.quizado

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.heathkev.quizado.databinding.ActivityMainBinding

const val DARK_MODE = "darkmode"
class MainActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val TOP_LEVEL_DESTINATIONS = setOf(
        R.id.startFragment,
        R.id.homeFragment,
        R.id.listFragment,
        R.id.leadersFragment,
        R.id.profileFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Update dark mode
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        val isDarkMode = sharedPref.getBoolean(DARK_MODE, false)

        if(isDarkMode){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        appBarConfiguration = AppBarConfiguration(
            TOP_LEVEL_DESTINATIONS,
            binding.mainDrawerLayout
        )

        setupNavigation()
    }

    /**
     * Called when the hamburger menu or back button are pressed on the Toolbar
     *
     * Delegate this to Navigation.
     */
    override fun onSupportNavigateUp() =
        navigateUp(findNavController(R.id.nav_host_fragment), appBarConfiguration) || super.onSupportNavigateUp()

    /**
     * Setup Navigation for this Activity
     */
    private fun setupNavigation() {
        // first find the nav controller
        val navController = findNavController(R.id.nav_host_fragment)

        setSupportActionBar(binding.toolbar)

        // then setup the action bar, tell it about the DrawerLayout
        setupActionBarWithNavController(navController, appBarConfiguration)

        // finally setup the left drawer (called a NavigationView)
        binding.navigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination: NavDestination, _ ->
            val toolBar = supportActionBar ?: return@addOnDestinationChangedListener
            when (destination.id) {
                R.id.homeFragment -> {
                    toolBar.useDefaultToolbar(false)

                    binding.listBtmNavView.visibility = View.VISIBLE
                }
                R.id.leadersFragment, R.id.profileFragment,R.id.listFragment -> {
                    toolBar.useDefaultToolbar(true)

                    binding.listBtmNavView.visibility = View.VISIBLE
                }
                else -> {
                    toolBar.useDefaultToolbar(true)

                    binding.listBtmNavView.visibility = View.GONE
                    binding.appBar.setExpanded(true, true)

                }
            }

            val isTopLevelDestination = TOP_LEVEL_DESTINATIONS.contains(destination.id)
            val lockMode = if (isTopLevelDestination) {
                DrawerLayout.LOCK_MODE_UNLOCKED
            } else {
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            }
            binding.mainDrawerLayout.setDrawerLockMode(lockMode)
        }

        binding.listBtmNavView.setOnNavigationItemSelectedListener {
            NavigationUI.onNavDestinationSelected(it, navController)
        }
    }

    override fun onBackPressed() {
        val btmNavView = binding.listBtmNavView
        if (btmNavView.visibility == View.VISIBLE){
            if(btmNavView.selectedItemId  != R.id.listFragment){
                btmNavView.selectedItemId = R.id.listFragment
            }else{
                if (doubleBackToExitPressedOnce) {
                    this@MainActivity.finish()
                    return
                }

                this.doubleBackToExitPressedOnce = true
                Toast.makeText(this, getString(R.string.please_click_back_again), Toast.LENGTH_SHORT).show()
                Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
            }
        }else{
            super.onBackPressed()
        }
    }

    private fun ActionBar.useDefaultToolbar(useToolbar: Boolean){
        if(useToolbar){
            this.setDisplayShowTitleEnabled(true)
            binding.heroImage.visibility = View.GONE
        }else{
            this.setDisplayShowTitleEnabled(false)
            binding.heroImage.visibility = View.VISIBLE
            this.show()
        }
    }
}