package com.binbard.geu.one

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.util.Linkify
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.binbard.geu.one.helpers.FirebaseUtils
import com.binbard.geu.one.helpers.PdfUtils
import com.binbard.geu.one.helpers.SharedPreferencesHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        private val sharedPreferencesHelper: SharedPreferencesHelper by lazy {
            SharedPreferencesHelper(requireContext())
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val campusPref = findPreference<Preference>("campus")
            val versionPref = findPreference<Preference>("version")
            val push_notifications = findPreference<SwitchPreferenceCompat>("push_notifications")
            val clearFilesPref = findPreference<Preference>("clear_files")
            val changelogsPref = findPreference<Preference>("changelogs")
            val privacyPolicyPref = findPreference<Preference>("privacy_policy")
            val termsPref = findPreference<Preference>("terms")
            val aboutPref = findPreference<Preference>("about")

            val mCampus =
                if (sharedPreferencesHelper.getCampus() == "deemed") "GEU (Deemed)" else "GEHU (Dehradun)"
            campusPref?.summary = mCampus

            val pInfo =
                requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            versionPref?.summary = "Version ${pInfo.versionName}"

            push_notifications?.isChecked = sharedPreferencesHelper.getPushNotifications()
            push_notifications?.setOnPreferenceChangeListener { _, newValue ->
                sharedPreferencesHelper.setPushNotifications(newValue as Boolean)
                val campus = sharedPreferencesHelper.getCampus()
                if (newValue) {
                    FirebaseUtils.subscribeTo("all")
                    FirebaseUtils.subscribeTo("notes")
                    FirebaseUtils.subscribeTo("$campus-feed")
                } else {
                    FirebaseUtils.unsubscribeFrom("all")
                    FirebaseUtils.unsubscribeFrom("notes")
                    FirebaseUtils.unsubscribeFrom("$campus-feed")
                }
                true
            }

            clearFilesPref?.setOnPreferenceClickListener {
                val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())

                builder.setTitle("Clear All Files")
                builder.setMessage("Are you sure you want to clear all files?")
                builder.setPositiveButton("Yes") { _, _ ->
                    PdfUtils.clearAllFiles(requireContext())
                    Toast.makeText(requireContext(), "All Files cleared", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.show()
                true
            }

            changelogsPref?.setOnPreferenceClickListener {
                val changelogSheet = ChangelogSheet()
                changelogSheet.show(requireActivity().supportFragmentManager, "changelog")
                true
            }

            privacyPolicyPref?.setOnPreferenceClickListener {
                val url = "https://geu-one-app.binbard.org/p/privacy-policy"
                val intent = Intent(requireContext(), ViewWebActivity::class.java)
                intent.putExtra("url", url)
                intent.putExtra("options", "bg_transparent")
                intent.putExtra("title", "Privacy Policy")
                startActivity(intent)
                true
            }

            termsPref?.setOnPreferenceClickListener {
                val url = "https://geu-one-app.binbard.org/p/terms-and-conditions"
                val intent = Intent(requireContext(), ViewWebActivity::class.java)
                intent.putExtra("url", url)
                intent.putExtra("options", "bg_transparent")
                intent.putExtra("title", "Terms and Conditions")
                startActivity(intent)
                true
            }

            aboutPref?.setOnPreferenceClickListener {
                val msg = "GEU One App (Version ${pInfo.versionName})\n\n" +
                        "- Developed by binbard\n\n" +
                        "Disclaimer: This app is not affiliated with Graphic Era University.\n\n" +
                        "If you have any feedback or suggestions, please let us know.\n\n" +
                        "Contact:\n" +
                        resources.getString(R.string.support_email)

                val s = SpannableString(msg)

                Linkify.addLinks(s, Linkify.EMAIL_ADDRESSES)

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("About")
                    .setMessage(s)
                    .setPositiveButton("OK") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
                true
            }


        }
    }
}