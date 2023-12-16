package com.binbard.geu.geuone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.binbard.geu.geuone.databinding.ActivityMainBinding
import com.binbard.geu.geuone.ui.notes.FragmentTitleListener
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(), FragmentTitleListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bottomNavView = binding.bottomNavView
        val bottomNavHost = supportFragmentManager.findFragmentById(R.id.bottomNavHost) as NavHostFragment
        val bottomNavController = bottomNavHost.findNavController()

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.bottomNavFeed,
                R.id.bottomNavRes,
                R.id.bottomNavNotes,
                R.id.bottomNavErp
            )
        )

        val toolbarFeed: Toolbar = findViewById(R.id.toolbarFeed)
        val feedSearchView: SearchView = toolbarFeed.findViewById(R.id.feedSearchView)
        feedSearchView.queryHint = "Search Feeds"
        feedSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(this@MainActivity, "Search", Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        supportActionBar?.hide()
        setSupportActionBar(findViewById(R.id.toolbarFeed))
        supportActionBar?.show()

        bottomNavController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.bottomNavFeed -> {
                    changeToolbar(findViewById(R.id.toolbarFeed))
                }
                R.id.bottomNavRes -> {
                    changeToolbar(findViewById(R.id.toolbarErp))
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

    private fun changeToolbar(toolbar: Toolbar){
        supportActionBar?.hide()
        setSupportActionBar(toolbar)
        bottomNavView.visibility = View.VISIBLE
        supportActionBar?.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun updateTitle(title: String) {
        supportActionBar?.title = title
    }

}