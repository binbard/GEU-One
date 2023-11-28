package com.binbard.geu.geuone.ui.erp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.binbard.geu.geuone.databinding.FragmentErpLoginBinding

class ErpLoginFragment: Fragment() {
    private lateinit var binding: FragmentErpLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErpLoginBinding.inflate(inflater, container, false)



        return binding.root
    }
}