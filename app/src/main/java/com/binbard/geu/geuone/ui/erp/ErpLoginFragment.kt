package com.binbard.geu.geuone.ui.erp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.binbard.geu.geuone.databinding.FragmentErpLoginBinding

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
            erpViewModel.erpId.value = text.toString()
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
            }

            erpViewModel.loginDone.value = true
            NavHostFragment.findNavController(this).navigateUp()
        }

        return binding.root
    }
}