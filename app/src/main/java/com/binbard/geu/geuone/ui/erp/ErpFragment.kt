package com.binbard.geu.geuone.ui.erp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.databinding.FragmentErpBinding

class ErpFragment: Fragment() {
    private lateinit var binding: FragmentErpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErpBinding.inflate(inflater, container, false)

        val erpViewModel = ViewModelProvider(this)[ErpViewModel::class.java]
        erpViewModel.erpText.observe(viewLifecycleOwner) {
            binding.textErp.text = it
        }

        return binding.root
    }
}

