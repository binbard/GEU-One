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
//    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
//    private var selectedCampus = ""
//    private lateinit var campusMap: Map<LinearLayout,String>

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInitialBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        val selectableItemBackground = ContextCompat.getDrawable(this, android.R.attr.selectableItemBackground)
//        binding.llHill.background = selectableItemBackground
//        binding.llDeemed.background = selectableItemBackground
//
//        sharedPreferencesHelper = SharedPreferencesHelper(this)
//
//        campusMap = mapOf(
//            binding.llDeemed to "deemed",
//            binding.llHill to "hill"
//        )
//
//        binding.llDeemed.setOnClickListener() {
//            changeCampus(binding.llDeemed)
//        }
//        binding.llHill.setOnClickListener() {
//            changeCampus(binding.llHill)
//        }
//
//        binding.btnNext.setOnClickListener() {
//            if(selectedCampus == "") {
//                Snack.showMsg(binding.root, "Please select campus")
//                return@setOnClickListener
//            }
//            sharedPreferencesHelper.setCampus(selectedCampus)
//            sharedPreferencesHelper.setInitDone(true)
//            startActivity(Intent(this, MainActivity::class.java))
//            finish()
//        }

    }

//    @SuppressLint("ResourceType")
//    private fun changeCampus(llCampus: LinearLayout){
//        binding.llDeemed.background = null
//        binding.llHill.background = null
//
//        llCampus.setBackgroundResource(R.drawable.rounded_corner)
//
//        selectedCampus = campusMap[llCampus].toString()
//    }
}