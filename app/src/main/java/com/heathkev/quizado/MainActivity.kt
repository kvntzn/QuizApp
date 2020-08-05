package com.heathkev.quizado

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.heathkev.quizado.databinding.ActivityMainBinding
import com.heathkev.quizado.databinding.NavHeaderBinding
import com.heathkev.quizado.ui.start.LoginViewModel
import com.heathkev.quizado.utils.setupWithNavController

const val DARK_MODE = "darkmode"
class MainActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    private var currentNavController: LiveData<NavController>? = null

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

        val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        //Navigation Header
        val navBinding: NavHeaderBinding = DataBindingUtil.inflate(layoutInflater, R.layout.nav_header, binding.navigationView, false)
        binding.navigationView.addHeaderView(navBinding.root)
        navBinding.viewModel = viewModel
        navBinding.lifecycleOwner = this
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
        navController = findNavController(R.id.nav_host_fragment)

        setSupportActionBar(binding.toolbar)

        // then setup the action bar, tell it about the DrawerLayout
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.navigationView.setupWithNavController(navController)

        val navGraphIds = listOf(R.navigation.nav_home)
        val controller = binding.listBtmNavView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_fragment,
            intent = intent
        )

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
            val lockMode = if (isTopLevelDestination && destination.id != R.id.startFragment) {
                DrawerLayout.LOCK_MODE_UNLOCKED
            } else {
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            }
            binding.mainDrawerLayout.setDrawerLockMode(lockMode)
        }

        // Whenever the selected controller changes, setup the action bar.
        controller.observe(this, Observer { navController ->
            setupActionBarWithNavController(navController)
        })
        currentNavController = controller
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val btmNavView = binding.listBtmNavView
        if (btmNavView.visibility == View.VISIBLE){
            if(btmNavView.selectedItemId  != R.id.homeFragment){
                btmNavView.selectedItemId = R.id.homeFragment
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