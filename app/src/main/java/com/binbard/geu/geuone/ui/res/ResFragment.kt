package com.binbard.geu.geuone.ui.res

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.databinding.FragmentResBinding
import com.binbard.geu.geuone.models.LoginStatus
import com.binbard.geu.geuone.ui.notes.PdfUtils

class ResFragment: Fragment() {
    private lateinit var binding: FragmentResBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResBinding.inflate(inflater, container, false)

        val rvm = ViewModelProvider(this).get(ResViewModel::class.java)
        rvm.resText.observe(viewLifecycleOwner) {
            binding.textRes.text = it
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_res_top, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.item_res_top_check-> {
                        Toast.makeText(requireContext(), "Profile", Toast.LENGTH_SHORT).show()
                        true
                    }
                    R.id.item_res_top_feedback -> {
                        true
                    }
                    R.id.item_res_top_clearfiles -> {
                        PdfUtils.clearAllFiles(requireContext())
                        Toast.makeText(requireContext(), "Cleared Downloaded Files", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

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