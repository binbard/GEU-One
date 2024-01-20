package com.binbard.geu.geuone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import androidx.core.view.MenuCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.binbard.geu.geuone.databinding.ActivityMainBinding
import com.binbard.geu.geuone.models.LoginStatus
import com.binbard.geu.geuone.ui.erp.ErpCacheHelper
import com.binbard.geu.geuone.ui.erp.ErpRepository
import com.binbard.geu.geuone.ui.erp.ErpViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var erpViewModel: ErpViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavView = binding.bottomNavView
        val bottomNavHost = supportFragmentManager.findFragmentById(R.id.bottomNavHost) as NavHostFragment
        val bottomNavController = bottomNavHost.findNavController()

        erpViewModel = ViewModelProvider(this)[ErpViewModel::class.java]
        if(erpViewModel.erpCacheHelper==null){
            erpViewModel.erpCacheHelper = ErpCacheHelper(this)
        }
        if(erpViewModel.erpRepository==null){
            erpViewModel.erpRepository = ErpRepository(erpViewModel.erpCacheHelper!!)
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.bottomNavFeed,
                R.id.bottomNavRes,
                R.id.bottomNavNotes,
                R.id.bottomNavErp
            )
        )

        bottomNavController.addOnDestinationChangedListener { _, destination, _ ->
            binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            when (destination.id) {
                R.id.bottomNavFeed -> {
                    changeToolbar(findViewById(R.id.toolbarFeed))
                }
                R.id.bottomNavRes -> {
                    changeToolbar(findViewById(R.id.toolbarRes))
                }
                R.id.bottomNavNotes -> {
                    changeToolbar(findViewById(R.id.toolbarNotes))
                }
                R.id.bottomNavErp -> {
                    changeToolbar(findViewById(R.id.toolbarErp))
                }
                R.id.erpLoginFragment -> {
                    supportActionBar?.hide()
                    bottomNavView.visibility = View.GONE
                }
            }
        }

        setupActionBarWithNavController(bottomNavController, appBarConfiguration)
        bottomNavView.setupWithNavController(bottomNavController)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_general_top, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_gen_settings -> {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.item_gen_feedback -> {
                Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }

    private fun changeToolbar(toolbar: Toolbar){
        supportActionBar?.hide()
        setSupportActionBar(toolbar)
        bottomNavView.visibility = View.VISIBLE
        supportActionBar?.show()
    }
}
