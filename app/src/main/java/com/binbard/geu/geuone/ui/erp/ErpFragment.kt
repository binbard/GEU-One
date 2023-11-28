package com.binbard.geu.geuone.ui.erp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.databinding.FragmentErpBinding
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

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

        val etShow = binding.etShow
        val etShow2 = binding.etShow2
        val etShow3 = binding.etShow3
        val etShow4 = binding.etShow4
        val imageView = binding.imageView
        val textView = binding.textView

        val erpCacheHelper = ErpCacheHelper(requireContext())
        val erpRepository = ErpRepository(erpCacheHelper)

//        etShow.visibility = View.GONE
//        etShow2.visibility = View.GONE
//        etShow3.visibility = View.GONE
        etShow4.visibility = View.GONE

        erpRepository.login(etShow, etShow2, etShow3, etShow4, imageView, textView)

        return binding.root
    }
}

