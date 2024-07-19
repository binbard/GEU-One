package com.binbard.geu.one

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.util.Linkify
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.binbard.geu.one.databinding.DialogFeedbackBinding
import com.binbard.geu.one.helpers.AlertMsg
import com.binbard.geu.one.helpers.NetUtils
import com.binbard.geu.one.helpers.SharedPreferencesHelper
import com.binbard.geu.one.ui.erp.ErpCacheHelper
import com.binbard.geu.one.ui.erp.ErpRepository
import com.binbard.geu.one.ui.erp.ErpViewModel
import com.binbard.geu.one.ui.erp.ChangelogSheet
import com.binbard.geu.one.ui.initial.InitialActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import java.net.URL
import java.time.ZoneId
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private lateinit var erpViewModel: ErpViewModel
    private lateinit var bottomNavController: NavController
    private var shouldGotoChangePassword = false
    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        sharedPreferencesHelper = SharedPreferencesHelper(this)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (intent.extras != null) {
            resolveClickAction()
            resolveHostIntent()
        }
        handleFirstTimeLaunch()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val bottomNavHost =
            supportFragmentManager.findFragmentById(R.id.bottomNavHost) as NavHostFragment
        bottomNavController = bottomNavHost.findNavController()
        if (shouldGotoChangePassword) gotoChangePassword()
        else resolveInitialFragment()

        erpViewModel = ViewModelProvider(this)[ErpViewModel::class.java]
        if (erpViewModel.erpCacheHelper == null) {
            erpViewModel.erpCacheHelper = ErpCacheHelper(this)
        }
        if (erpViewModel.erpRepository == null) {
            erpViewModel.erpRepository = ErpRepository(this, erpViewModel.erpCacheHelper!!)
        }
        if (erpViewModel.studentData.value == null) erpViewModel.erpCacheHelper?.loadLocalStudentData(
            erpViewModel
        )

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


        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo, AppUpdateType.IMMEDIATE, this, 1
                    )
                } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo, AppUpdateType.FLEXIBLE, this, 1
                    )
                }
            }
        }
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
            if (it.resultCode == RESULT_OK) {
                Toast.makeText(this, "Update Completed", Toast.LENGTH_SHORT).show()
            } else if (it.resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Update Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show()
            }
        }

        NetUtils.getAppUpdateInfo(this, BuildConfig.VERSION_CODE, erpViewModel)
        erpViewModel.updateAvailable.observe(this) {
            val priorityMap = mapOf(
                0 to "No", 1 to "Very Low", 2 to "Low", 3 to "Medium", 4 to "High", 5 to "Very High"
            )
            if (it > 0) {
                val repeat = if (it == 1 || it == 2) 0 else if (it == 3) 1 else -1
                AlertMsg.showMessage(
                    this,
                    "Update Available",
                    "A new update is available.\nPriority: ${priorityMap[it]}\n\nDo you want to update now?",
                    {
                        val intent = Intent()
                        intent.action = Intent.ACTION_VIEW
                        intent.data =
                            Uri.parse("https://play.google.com/store/apps/details?id=${packageName}")
                        startActivity(intent)
                    },
                    repeat
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager.completeUpdate()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_general_top, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_gen_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.item_gen_feedback -> {
                val dialogFeedbackBinding =
                    DialogFeedbackBinding.inflate(layoutInflater, null, false)
                dialogFeedbackBinding.chipBugReport.setOnClickListener {
                    dialogFeedbackBinding.etFeedback.hint =
                        "I encountered a bug while navigating from.."
                }
                dialogFeedbackBinding.chipReview.setOnClickListener {
                    dialogFeedbackBinding.etFeedback.hint = "My experience with this app has been.."
                }
                dialogFeedbackBinding.chipFeatureRequest.setOnClickListener {
                    dialogFeedbackBinding.etFeedback.hint = "I want a new feature in this app.."
                }

                var feedbackType = "review"
                val selectedChip = dialogFeedbackBinding.chipGroup.checkedChipId
                if (selectedChip == dialogFeedbackBinding.chipBugReport.id) feedbackType = "bug"
                else if (selectedChip == dialogFeedbackBinding.chipFeatureRequest.id) feedbackType =
                    "feature"
                else if (selectedChip == dialogFeedbackBinding.chipReview.id) feedbackType =
                    "review"

                MaterialAlertDialogBuilder(this).setTitle("Feedback")
                    .setMessage("Info: This is always shared anonymously. We can't revert back to you. Contact via email for any queries.")
                    .setView(dialogFeedbackBinding.root)
                    .setNegativeButton("Cancel") { dialog, which ->
                        // Negative btn pressed
                    }.setPositiveButton("SEND") { dialog, which ->
                        if (dialogFeedbackBinding.etFeedback.text.isEmpty()) return@setPositiveButton
                        val feedbackUrl = resources.getString(R.string.feedbackUrl)
                        NetUtils.sendFeedback(
                            this,
                            feedbackUrl,
                            feedbackType,
                            dialogFeedbackBinding.etFeedback.text.toString()
                        )
                    }.show()
                true
            }

            R.id.item_gen_contact_support -> {
                val email = resources.getString(R.string.support_email)
                val msg = "For any queries, contact us at:\n" + "Email: ${email}\n"

                val s = SpannableString(msg)
                Linkify.addLinks(s, Linkify.EMAIL_ADDRESSES)

                MaterialAlertDialogBuilder(this).setTitle("Contact Support").setMessage(s)
                    .setNegativeButton("Cancel") { dialog, which ->
                        // Negative btn pressed
                    }.setPositiveButton("Email") { dialog, which ->
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            setPackage("com.google.android.gm")
                            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                            putExtra(Intent.EXTRA_SUBJECT, "")
                            putExtra(Intent.EXTRA_TEXT, "")
                        }

                        if (intent.resolveActivity(packageManager) != null) {
                            startActivity(intent)
                        } else {
                            Toast.makeText(this, "Email app is not installed", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }.show()
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

    private fun handleFirstTimeLaunch() {
        if (sharedPreferencesHelper.getInitDone()) return
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
        if (uri.host == erpExtHost && uri.path == "/Account/ChangePassword") {
            shouldGotoChangePassword = true
        } else {
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(this, uri)
        }
    }

    private fun gotoChangePassword() {
        bottomNavController.navigate(R.id.ErpFragment)
    }

}
