package com.binbard.geu.geuone.ui.feed

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.MenuCompat
import androidx.core.view.MenuProvider
import androidx.lifecycle.lifecycleScope
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.addMenuProvider
import com.binbard.geu.geuone.databinding.ActivityFeedViewBinding
import com.binbard.geu.geuone.ui.notes.PdfUtils.openOrDownloadPdf
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class FeedViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedViewBinding

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFeedViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.postToolbar)
        supportActionBar?.title = ""

        val feedSlug = intent.getStringExtra("feedSlug") ?: ""

        addMenu()

        binding.webViewPost.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
//                if (url != null && url.endsWith(".pdf")) {
//                    openOrDownloadPdf(this@FeedViewActivity,url)
//                    return true
//                }
                val intent = CustomTabsIntent.Builder().build()
                intent.launchUrl(this@FeedViewActivity, android.net.Uri.parse(url))
                return true
            }
        }

        binding.webViewPost.setBackgroundColor(Color.TRANSPARENT)
        binding.webViewPost.settings.defaultFontSize = 18

        val hostUrl = resources.getString(R.string.hostUrl)
        binding.fabOpenInBrowser.setOnClickListener {
            val intent = CustomTabsIntent.Builder().build()
            intent.launchUrl(this@FeedViewActivity, android.net.Uri.parse("$hostUrl$feedSlug"))
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val feedPost = FeedHelper.fetchFeed(feedSlug)
                if (feedPost == null) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@FeedViewActivity,
                            "Failed to fetch feed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    return@withContext
                }
                withContext(Dispatchers.Main) {
                    binding.pbPost.visibility = View.GONE
                    binding.appBar.visibility = View.VISIBLE
                    binding.webViewPost.loadData(
                        getPostContent(feedPost.content),
                        "text/html",
                        "UTF-8"
                    )
                    binding.postToolbarLayout.title = getSpannedToolbarTitle(feedPost.title)
                    binding.tvPostTitle.text = getSpannedPostTitle(feedPost.title)
                    binding.fabOpenInBrowser.visibility = View.VISIBLE
                    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    val postTime = sdf.format(feedPost.date)
                    binding.tvPostModified.text = "Last Modified: $postTime"
                }
            }
        }

    }

    private fun getSpannedToolbarTitle(title: String): SpannableString {
        val spannable = SpannableString(title)
        spannable.setSpan(android.text.style.TypefaceSpan("monospace"), 0, title.length, 0)
        return spannable
    }

    private fun getSpannedPostTitle(title: String): SpannableString {
        val spannable = SpannableString(title)
        spannable.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            0,
            title.length,
            0
        )
        spannable.setSpan(
            android.text.style.ForegroundColorSpan(resources.getColor(com.google.android.material.R.color.material_dynamic_primary50)),
            0,
            title.length,
            0
        )
        return spannable
    }

    private fun getPostContent(content: String): String {
        val typedValue1 = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.colorAccent, typedValue1, true)
        val colorAccentHex = String.format("%06X", 0xFFFFFF and typedValue1.data)

        val typedValue2 = TypedValue()
        theme.resolveAttribute(com.google.android.material.R.attr.dividerColor, typedValue2, true)
        val dividerColorHex = String.format("%06X", 0xFFFFFF and typedValue2.data)

        val style =
            "<style>body{color:$colorAccentHex; font-family: font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; font-size: 18px; line-height: 1.5;} a{color:0077cc;}</style>"

        val tableStyle = "<style>table{border-collapse: collapse; width: 100%;} th, td {text-align: left; padding: 8px;} tr:nth-child(even){background-color: $dividerColorHex}</style>"
        val html = "<html><body>$style$tableStyle$content</body></html>";
        return html
    }

    private fun addMenu() {
        addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_general_top, menu)
                MenuCompat.setGroupDividerEnabled(menu, true)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.item_gen_settings -> {
                        Toast.makeText(this@FeedViewActivity, "Settings", Toast.LENGTH_SHORT).show()
                    }
                    R.id.item_gen_feedback -> {
                        Toast.makeText(this@FeedViewActivity, "Feedback", Toast.LENGTH_SHORT).show()
                    }
                }
                return true
            }
        })
    }
}