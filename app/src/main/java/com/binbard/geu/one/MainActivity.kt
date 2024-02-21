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
import androidx.browser.customtabs.CustomTabsIntent
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.binbard.geu.one.databinding.ActivityMainBinding
import com.binbard.geu.one.helpers.SharedPreferencesHelper
import com.binbard.geu.one.ui.erp.ErpCacheHelper
import com.binbard.geu.one.ui.erp.ErpRepository
import com.binbard.geu.one.ui.erp.ErpViewModel
import com.binbard.geu.one.ui.initial.InitialActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.net.URL


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var erpViewModel: ErpViewModel
    private lateinit var bottomNavController: NavController
    private var shouldGotoChangePassword = false

    override fun onCreate(savedInstanceState: Bundle?) {
        if (intent.extras != null) {
            resolveClickAction()
            resolveHostIntent()
        }
        sharedPreferencesHelper = SharedPreferencesHelper(this)
        handleFirstTimeLaunch()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val bottomNavHost =
            supportFragmentManager.findFragmentById(R.id.bottomNavHost) as NavHostFragment
        bottomNavController = bottomNavHost.findNavController()
        if(shouldGotoChangePassword) gotoChangePassword()
        else resolveInitialFragment()

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
                MaterialAlertDialogBuilder(this)
                    .setTitle("Feedback")
                    .setMessage("Info: This is always shared anonymously.")
                    .setView(R.layout.dialog_feedback)
                    .setNegativeButton("Cancel") { dialog, which ->
                        // Negative btn pressed
                    }
                    .setPositiveButton("SEND") { dialog, which ->
                        letsDoThis2()
                    }
                    .show()

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
        nanoMessagingService.sendNotification(
            this,
            "Feedback received. Thankyou for your valuable time."
        )
    }

    private fun handleFirstTimeLaunch() {
        if(sharedPreferencesHelper.getInitDone()) return
        val intent = Intent(this, InitialActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun resolveClickAction() {
        val clickAction = intent.extras?.getString("click_action") ?: return

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
        if (intent.extras != null) {
            val initialFragment = intent.extras!!.getString("initial_fragment") ?: return
            bottomNavController.popBackStack()
            val id = resources.getIdentifier(initialFragment, "id", packageName)
            if (id != 0) bottomNavController.navigate(id)
        }
    }

    private fun resolveHostIntent() {
        val uri = intent.data ?: return
        val url = URL(uri.scheme, uri.host, uri.path)
        val erpExtHost = resources.getString(R.string.erpExtHost)

        // Handle Change Password
        if(uri.host==erpExtHost && uri.path=="/Account/ChangePassword"){
            shouldGotoChangePassword = true
        } else{
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(this, uri)
        }
    }

    private fun gotoChangePassword(){
        bottomNavController.navigate(R.id.ErpFragment)
    }

}
