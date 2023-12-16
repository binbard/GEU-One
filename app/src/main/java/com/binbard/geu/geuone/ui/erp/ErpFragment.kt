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
    private lateinit var erpViewModel: ErpViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErpBinding.inflate(inflater, container, false)

        erpViewModel = ViewModelProvider(this)[ErpViewModel::class.java]

        erpCacheHelper = ErpCacheHelper(requireContext())
        erpRepository = ErpRepository(erpCacheHelper)

        erpCacheHelper.loadLocalData(erpViewModel)
        erpRepository.preLogin(erpViewModel)

        val toolbarErp: Toolbar = requireActivity().findViewById(R.id.toolbarErp)
        val btnErpMenu: ImageView = toolbarErp.findViewById(R.id.imgErpMenu)

        erpViewModel.loginStatus.observe(viewLifecycleOwner) {
            if(it==1){
                Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                setupErpFeatures()
                erpRepository.syncStudentData(erpCacheHelper)

                btnErpMenu.setOnClickListener {
                    if(sideSheetDialog.isShowing){
                        sideSheetDialog.dismiss()
                    } else {
                        sideSheetDialog.show()
                    }
                }
            } else if(it==0){
                Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView2, ErpLoginFragment()).commit()
            }
        }

        return binding.root
    }

    fun setupErpFeatures(){

        sideSheetDialog = SideSheetDialog(requireContext())
        sideSheetDialog.setContentView(R.layout.fragment_erp_sidesheet)
        sideSheetDialog.setSheetEdge(Gravity.START)

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


        erpViewModel.erpStudentId.observe(viewLifecycleOwner) {
            tvStuId?.text = it
        }
        erpViewModel.erpStudentName.observe(viewLifecycleOwner) {
            tvStuName?.text = it
        }
        erpViewModel.erpStudentImg.observe(viewLifecycleOwner) {
            val bitmap = BitmapHelper.stringToBitmap(it)
            tvStuImg?.setImageBitmap(bitmap)
        }

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