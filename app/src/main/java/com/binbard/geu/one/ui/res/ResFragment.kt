package com.binbard.geu.one.ui.res

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.MenuCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.FragmentResBinding
import com.binbard.geu.one.ui.notes.PdfUtils

class ResFragment: Fragment() {
    private lateinit var binding: FragmentResBinding
    private lateinit var rvm: ResViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResBinding.inflate(inflater, container, false)

        rvm = ViewModelProvider(this).get(ResViewModel::class.java)

        setHasOptionsMenu(true)

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
            PdfUtils.openOrDownloadPdf(requireContext(), "https://github.com/geu-one-static/files/blob/main/Mini%20Project%205th%20sem%20all.pdf?raw=true", "MiniProject5thSem")
        }

        requireActivity().findViewById<ImageView>(R.id.imgErpMenu).setOnClickListener(null)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_res_top, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_res_clearfiles -> {
                PdfUtils.clearAllFiles(requireContext())
                Toast.makeText(requireActivity(), "Cleared Files", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
    }

}