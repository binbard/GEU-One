package com.binbard.geu.one.ui.initial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.binbard.geu.one.databinding.FragmentDescribeBinding

class DescribeFragment: Fragment() {
    private lateinit var binding: FragmentDescribeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDescribeBinding.inflate(inflater, container, false)

        val fragmentManager = requireActivity().supportFragmentManager
        binding.viewPager.adapter = MyPagerAdapter(fragmentManager)

        return binding.root
    }

}