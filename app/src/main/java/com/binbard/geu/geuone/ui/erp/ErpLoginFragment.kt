package com.binbard.geu.geuone.ui.erp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.binbard.geu.geuone.databinding.FragmentErpLoginBinding
import com.google.android.material.snackbar.Snackbar

class ErpLoginFragment: Fragment() {
    private lateinit var binding: FragmentErpLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErpLoginBinding.inflate(inflater, container, false)

        val erpViewModel = ViewModelProvider(this)[ErpViewModel::class.java]

        val btLogin = binding.btLogin
        val etId = binding.etId
        val etPass = binding.etPass

        etId.doOnTextChanged { text, start, before, count ->
            erpViewModel.erpStudentId.value = text.toString()
        }

        etPass.doOnTextChanged { text, start, before, count ->
            erpViewModel.erpPassword.value = text.toString()
        }

        btLogin.setOnClickListener {
            val id = etId.text.toString()
            val pass = etPass.text.toString()
            val erpCacheHelper = ErpCacheHelper(requireContext())
            if(id != "" && pass!= "") {
                erpCacheHelper.saveStudentId(id)
                erpCacheHelper.savePassword(pass)
            } else{
                val snackbar = Snackbar.make(requireView(), "Please enter your credentials", Snackbar.LENGTH_SHORT)
                val snackbarView = snackbar.view
                val params = snackbarView.layoutParams as ViewGroup.MarginLayoutParams
                params.setMargins(params.leftMargin,
                    params.topMargin,
                    params.rightMargin,
                    params.bottomMargin + 180)
                snackbarView.layoutParams = params
                snackbar.setAction("OK") {
                    snackbar.dismiss()
                }
                snackbar.show()
            }

        }

        return binding.root
    }
}