package com.binbard.geu.one

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.binbard.geu.one.helpers.PdfUtils
import com.binbard.geu.one.helpers.SharedPreferencesHelper

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

            val mCampus = if (sharedPreferencesHelper.getCampus() == "deemed") "GEU (Deemed)" else "GEHU (Dehradun)"
            campusPref?.summary = mCampus

            val pInfo = requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
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

        }
    }
}