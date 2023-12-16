package com.binbard.geu.geuone.ui.erp

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.databinding.FragmentErpBinding
import com.binbard.geu.geuone.ui.erp.menu.ErpDefaultPage
import com.binbard.geu.geuone.ui.erp.menu.ErpStudentFragment
import com.binbard.geu.geuone.utils.BitmapHelper
import com.google.android.material.sidesheet.SideSheetDialog


class ErpFragment: Fragment() {
    private lateinit var binding: FragmentErpBinding
    private lateinit var sideSheetDialog: SideSheetDialog
    private lateinit var erpCacheHelper: ErpCacheHelper
    private lateinit var erpRepository: ErpRepository
    private lateinit var erpStudentFragment: ErpStudentFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErpBinding.inflate(inflater, container, false)

        val erpViewModel = ViewModelProvider(this)[ErpViewModel::class.java]

        erpCacheHelper = ErpCacheHelper(requireContext())
        erpRepository = ErpRepository(erpCacheHelper)

        erpRepository.preLogin(context)

        erpRepository.fetchImage(erpCacheHelper)

        if(erpViewModel.loginDone.value == false) {
            if(erpViewModel.loginDone.value == false) {
                erpViewModel.loginDone.value = true
//                NavHostFragment.findNavController(this).navigate(R.id.erpLoginFragment)
            }
        }

        sideSheetDialog = SideSheetDialog(requireContext())
        sideSheetDialog.setContentView(R.layout.fragment_erp_sidesheet)
        sideSheetDialog.setSheetEdge(Gravity.START)
        setupErpFeatures()

        val toolbarErp: Toolbar = requireActivity().findViewById(R.id.toolbarErp)
        val btnErpMenu: ImageView = toolbarErp.findViewById(R.id.imgErpMenu)
        btnErpMenu.setOnClickListener {
            if(sideSheetDialog.isShowing){
                sideSheetDialog.dismiss()
            } else {
                sideSheetDialog.show()
            }
        }

        return binding.root
    }

    fun setupErpFeatures(){

        erpStudentFragment = ErpStudentFragment()
        requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView2, erpStudentFragment).commit()

        val tvStuName: TextView? = sideSheetDialog.findViewById(R.id.tvStuName)
        val tvStuId: TextView? = sideSheetDialog.findViewById(R.id.tvStuId)
        val tvStuImg: ImageView? = sideSheetDialog.findViewById(R.id.tvStuImg)

        val btnStudent: TextView? = sideSheetDialog.findViewById(R.id.btnStudent)
        val btnAttendance: TextView? = sideSheetDialog.findViewById(R.id.btnAttendance)
        val btnTimetable: TextView? = sideSheetDialog.findViewById(R.id.btnTimetable)
        val btnSyllabus: TextView? = sideSheetDialog.findViewById(R.id.btnSyllabus)
        val btnExam: TextView? = sideSheetDialog.findViewById(R.id.btnExam)
        val btnMarks: TextView? = sideSheetDialog.findViewById(R.id.btnMarks)

        tvStuName?.text = erpCacheHelper.getStudentName()
        tvStuId?.text = erpCacheHelper.getStudentId()
        val bitmap = BitmapHelper.stringToBitmap(erpCacheHelper.getStudentImage())

        tvStuImg?.setImageBitmap(bitmap)

        btnStudent?.setOnClickListener {
            sideSheetDialog.dismiss()
            Toast.makeText(requireContext(), "Student", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView2, erpStudentFragment).commit()
        }

        btnAttendance?.setOnClickListener {
            sideSheetDialog.dismiss()
            Toast.makeText(requireContext(), "Attendance", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView2, ErpDefaultPage()).commit()
        }
    }

}