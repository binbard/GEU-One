package com.binbard.geu.one

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat.getSystemService
import com.binbard.geu.one.databinding.ActivityViewWebBinding


class ViewWebActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityViewWebBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val url = intent.getStringExtra("url") ?: "https://csitgeu.in/mp24/index.php"

        val options = intent.getStringExtra("options") ?: "none"

        val optionsList = options.split("|").map { it.trim() }

        val title = intent.getStringExtra("title") ?: "View"
        supportActionBar?.title = title

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url == null) return false
                if (url.startsWith("mailto:")) {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
                    startActivity(intent)
                    return true
                } else if (url.startsWith("http://") || url.startsWith("https://")) {
                    return if (optionsList.contains("links_open_disabled")) {
                        true
                    } else if (optionsList.contains("links_open_external")) {
                        openCustomTabs(url)
                        true
                    } else {
                        openCustomTabs(url)
                        true
                    }
                }
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (optionsList.contains("bg_transparent")) {
                    view?.loadUrl("javascript:(function() { document.body.style.backgroundColor = 'transparent'; })()")
                }
            }

            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String?,
                failingUrl: String?
            ) {
                binding.webView.loadUrl("file:///android_asset/error.html")
            }
        }

        binding.webView.setBackgroundColor(Color.TRANSPARENT)
        binding.webView.settings.javaScriptEnabled = true

        if(isNetworkAvailable()){
            binding.webView.loadUrl(url)
        } else{
            binding.webView.loadUrl("file:///android_asset/error.html")
        }

    }

    private fun openCustomTabs(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }


    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }


}