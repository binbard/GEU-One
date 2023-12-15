package com.binbard.geu.geuone.ui.erp

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.databinding.FragmentErpBinding
import com.google.android.material.sidesheet.SideSheetDialog


class ErpFragment: Fragment() {
    private lateinit var binding: FragmentErpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErpBinding.inflate(inflater, container, false)

        val erpViewModel = ViewModelProvider(this)[ErpViewModel::class.java]

        val etShow = binding.etShow
        val etShow2 = binding.etShow2
        val etShow3 = binding.etShow3
        val etShow4 = binding.etShow4
        val imageView = binding.imageView
        val textView = binding.textView

        val erpCacheHelper = ErpCacheHelper(requireContext())
        val erpRepository = ErpRepository(erpCacheHelper)

        if(erpViewModel.loginDone.value == false) {
            if(erpViewModel.loginDone.value == false) {
                erpViewModel.loginDone.value = true
                NavHostFragment.findNavController(this).navigate(com.binbard.geu.geuone.R.id.erpLoginFragment)
            }
        } else{
            erpRepository.preLogin(etShow, etShow2, etShow3, etShow4, imageView, textView)
        }

        erpViewModel.erpText.observe(viewLifecycleOwner) {
            binding.textErp.text = it
        }

        binding.textErp.visibility = View.GONE

//        etShow.visibility = View.GONE
//        etShow2.visibility = View.GONE
//        etShow3.visibility = View.GONE
//        etShow4.visibility = View.GONE

        val toolbarErp: Toolbar = requireActivity().findViewById(R.id.toolbarErp)
        val btnErpMenu: ImageView = toolbarErp.findViewById(R.id.imgErpMenu)
        btnErpMenu.setOnClickListener {
            val sideSheetDialog = SideSheetDialog(requireContext())
            sideSheetDialog.setContentView(com.binbard.geu.geuone.R.layout.fragment_erp2)
            sideSheetDialog.setSheetEdge(Gravity.START)
            sideSheetDialog.show()
        }

        binding.buttonGo.setOnClickListener {
            erpRepository.login(etShow, etShow2, etShow3, textView)
        }

        binding.buttonBro.setOnClickListener {
            val sideSheetDialog = SideSheetDialog(requireContext())
            sideSheetDialog.setContentView(com.binbard.geu.geuone.R.layout.fragment_erp2)
            sideSheetDialog.setSheetEdge(Gravity.START)
            sideSheetDialog.show()
        }

        return binding.root
    }

}