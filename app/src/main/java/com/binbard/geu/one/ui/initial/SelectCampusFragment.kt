package com.binbard.geu.one.ui.initial

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.binbard.geu.one.MainActivity
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.FragmentSelectCampusBinding
import com.binbard.geu.one.helpers.FirebaseUtils
import com.binbard.geu.one.helpers.SharedPreferencesHelper
import com.binbard.geu.one.helpers.Snack

class SelectCampusFragment: Fragment() {
    private lateinit var binding: FragmentSelectCampusBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var selectedCampus = ""
    private lateinit var campusMap: Map<LinearLayout,String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSelectCampusBinding.inflate(inflater, container, false)

        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())

        campusMap = mapOf(
            binding.llDeemed to "deemed",
            binding.llHill to "hill"
        )

        binding.llDeemed.setOnClickListener() {
            changeCampus(binding.llDeemed)
        }
        binding.llHill.setOnClickListener() {
            changeCampus(binding.llHill)
        }

        binding.btnNext.setOnClickListener() {
            if(selectedCampus == "") {
                Snack.showMsg(binding.root, "Please select campus")
                return@setOnClickListener
            }
            sharedPreferencesHelper.setCampus(selectedCampus)
            sharedPreferencesHelper.setInitDone(true)
            sharedPreferencesHelper.setInstalledTime(System.currentTimeMillis())
            FirebaseUtils.subscribeTo(selectedCampus)
            FirebaseUtils.subscribeTo("$selectedCampus~feed")
            FirebaseUtils.subscribeTo("$selectedCampus~resources")
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }

        return binding.root
    }

    @SuppressLint("ResourceType")
    private fun changeCampus(llCampus: LinearLayout){
        binding.llDeemed.background = null
        binding.llHill.background = null

        try{
            llCampus.setBackgroundResource(R.drawable.rounded_corner)
        } catch(e: Exception){
            llCampus.setBackgroundResource(R.drawable.rounded_corner_fallback)
//            llCampus.setBackgroundColor(Color.LTGRAY)
        }

        selectedCampus = campusMap[llCampus].toString()
    }

}