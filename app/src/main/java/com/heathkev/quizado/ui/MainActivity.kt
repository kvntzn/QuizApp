package com.heathkev.quizado.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.heathkev.quizado.R
import com.heathkev.quizado.databinding.NavHeaderBinding
import com.heathkev.quizado.result.EventObserver
import com.heathkev.quizado.ui.signin.SignOutDialogFragment
import com.heathkev.quizado.utils.*
import com.heathkev.quizado.widget.NavigationBarContentFrameLayout
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity(),
    NavigationHost {

    companion object {
        private const val DIALOG_SIGN_OUT = "dialog_sign_out"
    }

    private var doubleBackToExitPressedOnce = false

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    private lateinit var content: FrameLayout
    private lateinit var drawer: DrawerLayout
    private lateinit var statusScrim: View
    private lateinit var navigationView: NavigationView
    private lateinit var navHeaderBinding: NavHeaderBinding
    private lateinit var btmNavigationView: BottomNavigationView
    private lateinit var navController: NavController
    private var navHostFragment: NavHostFragment? = null

    private val TOP_LEVEL_DESTINATIONS = setOf(
        R.id.homeFragment,
        R.id.listFragment,
        R.id.leadersFragment,
        R.id.profileFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        updateForTheme(mainActivityViewModel.currentTheme)

        setContentView(R.layout.activity_main)

        val drawerContainer: NavigationBarContentFrameLayout = findViewById(R.id.drawer_container)
        // Let's consume any
        drawerContainer.setOnApplyWindowInsetsListener { v, insets ->
            // Let the view draw it's navigation bar divider
            v.onApplyWindowInsets(insets)

            // Consume any horizontal insets and pad all content in. There's not much we can do
            // with horizontal insets
            v.updatePadding(
                left = insets.systemWindowInsetLeft,
                right = insets.systemWindowInsetRight
            )
            insets.replaceSystemWindowInsets(
                0, insets.systemWindowInsetTop,
                0, insets.systemWindowInsetBottom
            )
        }

        content = findViewById(R.id.content_container)
        content.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        // Make the content ViewGroup ignore insets so that it does not use the default padding
        content.setOnApplyWindowInsetsListener(NoopWindowInsetsListener)

        statusScrim = findViewById(R.id.status_bar_scrim)
        statusScrim.setOnApplyWindowInsetsListener(HeightTopWindowInsetsListener)

        drawer = findViewById(R.id.main_drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        btmNavigationView = findViewById(R.id.list_btm_nav_view)

        //Navigation Header
        navHeaderBinding = NavHeaderBinding.inflate(layoutInflater).apply {
            viewModel = mainActivityViewModel
            lifecycleOwner = this@MainActivity
        }

        // Navigation view and Header
        navigationView.apply {
            val menuView = findViewById<RecyclerView>(R.id.design_navigation_view)

            navHeaderBinding.root.doOnApplyWindowInsets { v, insets, padding ->
                v.updatePadding(top = padding.top + insets.systemWindowInsetTop)
                // NavigationView doesn't dispatch insets to the menu view, so pad the bottom here.
                menuView?.updatePadding(bottom = insets.systemWindowInsetBottom)
            }

            addHeaderView(navHeaderBinding.root)
        }
        // Nav host and controller
        setupNavigation()

        mainActivityViewModel.theme.observe(this, Observer(::updateForTheme))

        mainActivityViewModel.navigateToSignOutDialogAction.observe(this, EventObserver {
            openSignOutDialog()
        })
    }

    private fun openSignOutDialog() {
        SignOutDialogFragment().show(supportFragmentManager, DIALOG_SIGN_OUT)
    }

    private fun setupNavigation() {
        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment?

        navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val currentNavId = destination.id

            val isTopLevelDestination = TOP_LEVEL_DESTINATIONS.contains(currentNavId)
            if (isTopLevelDestination) {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                btmNavigationView.visibility = View.VISIBLE
            } else {
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                btmNavigationView.visibility = View.GONE
            }
        }

        // then setup the action bar, tell it about the DrawerLayout
        navigationView.setupWithNavController(navController)
        btmNavigationView.setupWithNavController(navController)
        btmNavigationView.setOnNavigationItemReselectedListener {
            if (btmNavigationView.selectedItemId != it.itemId)
                return@setOnNavigationItemReselectedListener
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(navigationView) && drawer.shouldCloseDrawerFromBackPress()) {
            closeDrawer()
        } else if (btmNavigationView.visibility == View.VISIBLE) {
            closeApp(btmNavigationView)
        } else {
            super.onBackPressed()
        }
    }

    private fun closeApp(btmNavView: BottomNavigationView) {
        if (btmNavView.selectedItemId != R.id.homeFragment) {
            btmNavView.selectedItemId =
                R.id.homeFragment
        } else {
            if (doubleBackToExitPressedOnce) {
                this@MainActivity.finish()
                return
            }

            this.doubleBackToExitPressedOnce = true
            Toast.makeText(
                this,
                getString(R.string.please_click_back_again),
                Toast.LENGTH_SHORT
            ).show()
            Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
        }
    }

    private fun closeDrawer() {
        drawer.closeDrawer(GravityCompat.START)
    }

    override fun registerToolbarWithNavigation(toolbar: Toolbar) {
        val appBarConfiguration = AppBarConfiguration(TOP_LEVEL_DESTINATIONS, drawer)
        toolbar.setupWithNavController(navController, appBarConfiguration)
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        getCurrentFragment()?.onUserInteraction()
    }

    private fun getCurrentFragment(): MainNavigationFragment? {
        return navHostFragment
            ?.childFragmentManager
            ?.primaryNavigationFragment as? MainNavigationFragment
    }
}