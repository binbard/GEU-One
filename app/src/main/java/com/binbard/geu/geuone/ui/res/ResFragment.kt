package com.binbard.geu.geuone.ui.res

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.databinding.FragmentResBinding
import com.binbard.geu.geuone.ui.notes.PdfUtils

class ResFragment: Fragment() {
    private lateinit var binding: FragmentResBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResBinding.inflate(inflater, container, false)

        val resViewModel = ViewModelProvider(this).get(ResViewModel::class.java)
        resViewModel.resText.observe(viewLifecycleOwner) {
            binding.textRes.text = it
        }

        val intent = CustomTabsIntent.Builder().build()

        binding.btAcmClub.setOnClickListener {
            intent.launchUrl(it.context, "https://geu-acm.surge.sh/".toUri())
        }
        binding.btIeeeClub.setOnClickListener {
            intent.launchUrl(it.context, "https://geu-ieee.github.io/".toUri())
        }
        binding.btFacultyList.setOnClickListener {
            intent.launchUrl(it.context, "https://geu.ac.in/computer-science-and-engineering/faculty/".toUri())
        }
        binding.btSyllabus.setOnClickListener {
            intent.launchUrl(it.context, "https://csitgeu.in/wp/2017/08/16/all-semester-syllabus/".toUri())
        }
        binding.btMiniproject.setOnClickListener {
//            PdfUtils.openPdfWithName(it.context, "MiniProject")
            PdfUtils.openOrDownloadPdf(it.context, "https://github.com/geu-one-static/files/blob/main/Mini%20Project%205th%20sem%20all.pdf?raw=true", "MiniProject5thSem")
        }

        requireActivity().findViewById<ImageView>(R.id.imgErpMenu).setOnClickListener(null)

        return binding.root
    }
}