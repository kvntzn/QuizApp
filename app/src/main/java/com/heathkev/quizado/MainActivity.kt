package com.heathkev.quizado

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
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

    lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

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

        // APP bar
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.startFragment,
                R.id.listFragment,
                R.id.leadersFragment,
                R.id.profileFragment
            ),
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
        navigateUp(findNavController(R.id.nav_host_fragment), appBarConfiguration)

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
                R.id.listFragment -> {
                    toolBar.useDefaultToolbar(false)

                    binding.listBtmNavView.visibility = View.VISIBLE
                }
                R.id.leadersFragment -> {
                    toolBar.useDefaultToolbar(true)

                    binding.listBtmNavView.visibility = View.VISIBLE
                }
                R.id.profileFragment -> {
                    toolBar.useDefaultToolbar(true)

                    binding.listBtmNavView.visibility = View.VISIBLE
                }
                else -> {
                    toolBar.useDefaultToolbar(true)

                    binding.listBtmNavView.visibility = View.GONE
                    binding.appBar.setExpanded(true, true)
                }
            }
        }

        binding.listBtmNavView.setOnNavigationItemSelectedListener {
            NavigationUI.onNavDestinationSelected(it, navController)
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