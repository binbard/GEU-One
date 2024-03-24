package com.binbard.geu.one

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.text.util.Linkify
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
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
            val clearFilesPref = findPreference<Preference>("clear_files")
            val privacyPolicyPref = findPreference<Preference>("privacy_policy")
            val termsPref = findPreference<Preference>("terms")
            val aboutPref = findPreference<Preference>("about")

            val mCampus =
                if (sharedPreferencesHelper.getCampus() == "deemed") "GEU (Deemed)" else "GEHU (Dehradun)"
            campusPref?.summary = mCampus

            val pInfo =
                requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            versionPref?.summary = "Version ${pInfo.versionName}"

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
                        "geu.one.app@binbard.org"

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