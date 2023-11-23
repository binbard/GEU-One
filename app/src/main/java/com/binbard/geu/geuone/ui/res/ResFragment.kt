package com.binbard.geu.geuone.ui.res

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.geuone.databinding.FragmentResBinding

class ResFragment: Fragment() {
    private lateinit var binding: FragmentResBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentResBinding.inflate(inflater, container, false)

        val resViewModel = ViewModelProvider(this).get(ResViewModel::class.java)
        resViewModel.resText.observe(viewLifecycleOwner) {
            binding.textRes.text = it
        }

        return binding.root
    }
}