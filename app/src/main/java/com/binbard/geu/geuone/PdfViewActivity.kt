package com.binbard.geu.geuone

import android.os.Bundle
import android.view.Window
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.binbard.geu.geuone.databinding.ActivityPdfViewBinding

class PdfViewActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityPdfViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPdfViewBinding.inflate(layoutInflater)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)


        val url = "https://www.africau.edu/images/default/sample.pdf"

        binding.webview.settings.javaScriptEnabled = true

        binding.webview.loadUrl("https://docs.google.com/gview?embedded=true&url=$url")


        setContentView(binding.root)
    }
}