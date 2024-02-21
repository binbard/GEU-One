package com.binbard.geu.one.ui.initial

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.binbard.geu.one.MainActivity
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.ActivityInitialBinding
import com.binbard.geu.one.helpers.SharedPreferencesHelper
import com.binbard.geu.one.helpers.Snack


class InitialActivity: AppCompatActivity() {
    private lateinit var binding: ActivityInitialBinding
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInitialBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}