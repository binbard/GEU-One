package com.binbard.geu.one

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.MenuCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import com.binbard.geu.one.databinding.ActivityFeedViewBinding
import com.binbard.geu.one.databinding.DialogFeedbackBinding
import com.binbard.geu.one.helpers.NetUtils
import com.binbard.geu.one.ui.feed.FeedHelper
import com.binbard.geu.one.helpers.PdfUtils
import com.binbard.geu.one.helpers.SharedPreferencesHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class FeedViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedViewBinding
    private lateinit var feedUrl: String
    private lateinit var feedUrlView: String

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFeedViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.postToolbar)
        supportActionBar?.title = ""

        val feedSlug = intent.getStringExtra("feedSlug") ?: ""

        addMenu()

        binding.webViewPost.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null && url.endsWith(".pdf")) {
                    PdfUtils.openOrDownloadPdf(this@FeedViewActivity, url, "", true)
                    return true
                }
                val intent = CustomTabsIntent.Builder().build()
                intent.launchUrl(this@FeedViewActivity, android.net.Uri.parse(url))
                return true
            }
        }

        try{
            if (resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                binding.postToolbarLayout.setBackgroundColor(resources.getColor(com.google.android.material.R.color.material_dynamic_primary50))
                binding.postToolbarLayout.setContentScrimColor(resources.getColor(com.google.android.material.R.color.material_dynamic_primary30))
            } else{
                binding.postToolbarLayout.setBackgroundColor(resources.getColor(com.google.android.material.R.color.material_dynamic_primary70))
                binding.postToolbarLayout.setContentScrimColor(resources.getColor(com.google.android.material.R.color.material_dynamic_primary60))
            }
        } catch (e: Exception){
            Log.e("FeedViewActivity", "Error in setting toolbar color", e)
        }

        binding.webViewPost.setBackgroundColor(Color.TRANSPARENT)
        binding.webViewPost.settings.defaultFontSize = 18

        val sharedPreferencesHelper = SharedPreferencesHelper(this)
        val campus = sharedPreferencesHelper.getCampus()

        if (campus == "deemed") {
            feedUrl = "${resources.getString(R.string.feedsHostDeemed)}$feedSlug?json=post"
            feedUrlView = "${resources.getString(R.string.feedsHostDeemed)}$feedSlug"
        } else {
            feedUrl =
                "${resources.getString(R.string.feedsHostHill)}wp-json/wp/v2/posts/?slug=$feedSlug"
            feedUrlView = "${resources.getString(R.string.feedsHostHill)}$feedSlug"
        }

        binding.fabOpenInBrowser.setOnClickListener {
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(this@FeedViewActivity, android.net.Uri.parse(feedUrlView))
        }

        val feedHelper = FeedHelper(this)

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val feedPost = feedHelper.fetchFeed(feedUrl, campus)
                if (feedPost == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@FeedViewActivity,
                            "Failed to Load",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@withContext
                }
                withContext(Dispatchers.Main) {
                    binding.pbPost.visibility = View.GONE
                    binding.appBar.visibility = View.VISIBLE
                    binding.webViewPost.settings.javaScriptEnabled = true
                    val mTitle = parseDecode(feedPost.title)
                    val mContent = parseDecode(feedPost.content)
                    binding.webViewPost.loadData(
                        getPostContent(mContent, binding.tvPostModified.currentTextColor),
                        "text/html",
                        "UTF-16"
                    )
                    binding.postToolbarLayout.title = getSpannedToolbarTitle(mTitle)
                    binding.tvPostTitle.text = getSpannedPostTitle(mTitle)
                    binding.fabOpenInBrowser.visibility = View.VISIBLE
                    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    val postTime = sdf.format(feedPost.modified)
                    binding.tvPostModified.text = "Last Modified: $postTime"
                }
            }
        }

    }


    private fun parseDecode(txt: String): String{
        var text = txt.replace("\\\\u([0-9A-Fa-f]{4})".toRegex()) {     // Decode unicode
            String(Character.toChars(it.groupValues[1].toInt(radix = 16)))
        }
        text = text.replace("&#[0-9]+;".toRegex()) {                  // Decode html entities
            String(Character.toChars(it.value.substringAfter("&#").substringBefore(";").toInt()))
        }
        return text
    }


    private fun getSpannedToolbarTitle(title: String): SpannableString {
        val spannable = SpannableString(title)
        spannable.setSpan(android.text.style.TypefaceSpan("monospace"), 0, title.length, 0)
        return spannable
    }

    private fun getSpannedPostTitle(title: String): SpannableString {
        val spannable = SpannableString(title)
        val typedValue1 = TypedValue()
        theme.resolveAttribute(android.R.attr.colorPrimary, typedValue1, true)
        val colorAccentHex = String.format("%06X", 0xFFFFFF and typedValue1.data)
        spannable.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            0,
            title.length,
            0
        )
        spannable.setSpan(
            android.text.style.ForegroundColorSpan(Color.parseColor("#$colorAccentHex")),
            0,
            title.length,
            0
        )
        return spannable
    }

    private fun getPostContent(content: String, colorInt: Int): String {
        val typedValue1 = TypedValue()
//        theme.resolveAttribute(android.R.attr.colorAccent, typedValue1, true)
//        val colorAccentHex = String.format("%06X", 0xFFFFFF and typedValue1.data)

        val colorAccentHex = String.format("%06X", 0xFFFFFF and colorInt)

        val rootStyle =
            ".wrapper{overflow-x:scroll;} a{white-space:nowrap;overflow:hidden;text-overflow:ellipsis;display:inline-block;max-width:100%;}"
        val bodyStyle =
            "body{color:$colorAccentHex; font-family: font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; font-size: 18px; line-height: 1.5;} a{color:0077cc;}"

        val js = """
              var bodyElements = document.body.children;
              for (var i = 0; i < bodyElements.length; i++) {
                var container = document.createElement('div');
                container.classList.add('wrapper');
                container.appendChild(bodyElements[i]);
                document.body.insertBefore(container, bodyElements[i]);
              }
        """.trimIndent()

        val html =
            "<html><style>$rootStyle$bodyStyle</style><body>$content<script >$js</script></body></html>";
        return html
    }

    private fun addMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_general_top, menu)
                MenuCompat.setGroupDividerEnabled(menu, true)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.item_gen_settings -> {
                        val intent = Intent(this@FeedViewActivity, SettingsActivity::class.java)
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

                        MaterialAlertDialogBuilder(this@FeedViewActivity)
                            .setTitle("Feedback")
                            .setMessage("Info: This is always shared anonymously.")
                            .setView(dialogFeedbackBinding.root)
                            .setNegativeButton("Cancel") { dialog, which ->
                                // Negative btn pressed
                            }
                            .setPositiveButton("SEND") { dialog, which ->
                                if(dialogFeedbackBinding.etFeedback.text.isEmpty()) return@setPositiveButton
                                val feedbackUrl = resources.getString(R.string.feedbackUrl)
                                NetUtils.sendFeedback(
                                    this@FeedViewActivity,
                                    feedbackUrl,
                                    feedbackType,
                                    dialogFeedbackBinding.etFeedback.text.toString()
                                )
                            }
                            .show()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        })
    }
}