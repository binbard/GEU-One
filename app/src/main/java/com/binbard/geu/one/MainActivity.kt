package com.binbard.geu.one

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.binbard.geu.one.databinding.ActivityMainBinding
import com.binbard.geu.one.ui.erp.ErpCacheHelper
import com.binbard.geu.one.ui.erp.ErpRepository
import com.binbard.geu.one.ui.erp.ErpViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var erpViewModel: ErpViewModel
    private lateinit var bottomNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        resolveClickAction()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val bottomNavHost =
            supportFragmentManager.findFragmentById(R.id.bottomNavHost) as NavHostFragment
        bottomNavController = bottomNavHost.findNavController()
        resolveInitialFragment()

        erpViewModel = ViewModelProvider(this)[ErpViewModel::class.java]
        if (erpViewModel.erpCacheHelper == null) {
            erpViewModel.erpCacheHelper = ErpCacheHelper(this)
        }
        if (erpViewModel.erpRepository == null) {
            erpViewModel.erpRepository = ErpRepository(erpViewModel.erpCacheHelper!!)
        }

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.ResourcesFragment, R.id.FeedFragment, R.id.NotesFragment, R.id.ErpFragment
            )
        )
        bottomNavController.addOnDestinationChangedListener { _, destination, _ ->
            binding.drawerLayout.setDrawerLockMode(androidx.drawerlayout.widget.DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            when (destination.id) {
                R.id.FeedFragment -> {
                    changeToolbar(findViewById(R.id.toolbarFeed))
                }
                R.id.ResourcesFragment -> {
                    changeToolbar(findViewById(R.id.toolbarRes))
                }
                R.id.NotesFragment -> {
                    changeToolbar(findViewById(R.id.toolbarNotes))
                }
                R.id.ErpFragment -> {
                    changeToolbar(findViewById(R.id.toolbarErp))
                }
                R.id.ErpLoginFragment -> {
                    supportActionBar?.hide()
                    binding.bottomNavView.visibility = View.GONE
                }
            }
        }

        setupActionBarWithNavController(bottomNavController, appBarConfiguration)
        binding.bottomNavView.setupWithNavController(bottomNavController)

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
//                Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show()
                letsDoThis2()
                true
            }
            else -> false
        }
    }

    private fun changeToolbar(toolbar: Toolbar) {
        supportActionBar?.hide()
        setSupportActionBar(toolbar)
        binding.bottomNavView.visibility = View.VISIBLE
        supportActionBar?.show()
    }

    private fun letsDoThis2() {
        val nanoMessagingService = NanoMessagingService()
        nanoMessagingService.sendNotification(this, "Test Message: Feedback received")
    }

    private fun resolveClickAction() {
        if (intent.extras == null) return
        val clickAction = intent.extras!!.getString("click_action") ?: ""

        var cls: Class<*>
        try {
            cls = Class.forName(clickAction)
        } catch (e: ClassNotFoundException) {
            Log.e("ResolveClickAction", "Failed to resolve")
            cls = MainActivity::class.java
            return
        }
        if (cls == MainActivity::class.java) return
        val i = Intent(this, cls)
        i.putExtras(intent.extras!!)
        startActivity(i)
    }

    private fun resolveInitialFragment() {
        if (intent.extras != null){
            val initialFragment = intent.extras!!.getString("initial_fragment") ?: return
            bottomNavController.popBackStack()
            val id = resources.getIdentifier(initialFragment, "id", packageName)
            if(id!=0) bottomNavController.navigate(id)
        }
    }

}
