package com.binbard.geu.geuone.ui.erp

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.databinding.FragmentErpBinding
import com.binbard.geu.geuone.ui.Snack
import com.binbard.geu.geuone.ui.erp.menu.ErpDefaultPage
import com.binbard.geu.geuone.ui.erp.menu.ErpStudentFragment
import com.binbard.geu.geuone.utils.BitmapHelper
import com.google.android.material.sidesheet.SideSheetDialog
import com.google.android.material.snackbar.Snackbar


class ErpFragment : Fragment() {
    private lateinit var binding: FragmentErpBinding
    private lateinit var sideSheetDialog: SideSheetDialog
    private lateinit var erpCacheHelper: ErpCacheHelper
    private lateinit var erpRepository: ErpRepository
    private lateinit var erpStudentFragment: ErpStudentFragment
    private lateinit var erpViewModel: ErpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErpBinding.inflate(inflater, container, false)

        erpViewModel = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        erpCacheHelper = erpViewModel.erpCacheHelper!!
        erpRepository = erpViewModel.erpRepository!!

        val loginStatus = erpViewModel.erpCacheHelper!!.getLoginStatus()

        erpCacheHelper.loadLocalData(erpViewModel)
        if (loginStatus == 1){
            setupErpFeatures()
            if(erpViewModel.loginStatus.value!=2) erpRepository.preLogin(erpViewModel)
        } else{
            setupErpFeatures(unset=true)
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView2, ErpLoginFragment()).commit()
        }

        erpViewModel.loginStatus.observe(viewLifecycleOwner) {
            if(it==1){
                setupErpFeatures()
                childFragmentManager.popBackStack()
                childFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView2, erpViewModel.erpOptionStudent).commit()

                erpRepository.syncStudentData(erpViewModel)

                erpViewModel.loginStatus.value = 2
                erpViewModel.erpCacheHelper!!.saveLoginStatus(1)
            }
            else if(it==0){
                setupErpFeatures(unset=true)
                if(loginStatus==1){
                    Snack.showMsg(requireActivity().findViewById(android.R.id.content), "Session Expired")
                    childFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView2, ErpLoginFragment()).commit()
                } else{
                    Snack.showMsg(requireActivity().findViewById(android.R.id.content), "Wrong Credentials")
                }
                erpViewModel.loginStatus.value = -1
            }
            else if(it==-2){
                Toast.makeText(requireContext(), "Logged Out", Toast.LENGTH_SHORT).show()
                setupErpFeatures(unset=true)
                erpViewModel.erpCacheHelper!!.saveLoginStatus(0)
                erpViewModel.erpCacheHelper!!.clearLocalData()
                erpViewModel.loginStatus.value = -1
                childFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView2, ErpLoginFragment()).commit()
            }
        }

        return binding.root
    }

    fun setupErpFeatures(unset: Boolean = false) {

        sideSheetDialog = SideSheetDialog(requireContext())
        sideSheetDialog.setContentView(R.layout.fragment_erp_sidesheet)
        sideSheetDialog.setSheetEdge(Gravity.START)

        val toolbarErp: Toolbar = requireActivity().findViewById(R.id.toolbarErp)
        val btnErpMenu: ImageView = toolbarErp.findViewById(R.id.imgErpMenu)

        if(unset){
            btnErpMenu.setOnClickListener(null)
            return
        }

        val tvStuName: TextView? = sideSheetDialog.findViewById(R.id.tvStuName)
        val tvStuId: TextView? = sideSheetDialog.findViewById(R.id.tvStuId)
        val tvStuImg: ImageView? = sideSheetDialog.findViewById(R.id.tvStuImg)

        val btnStudent: TextView? = sideSheetDialog.findViewById(R.id.btnStudent)
        val btnAttendance: TextView? = sideSheetDialog.findViewById(R.id.btnAttendance)
        val btnTimetable: TextView? = sideSheetDialog.findViewById(R.id.btnTimetable)
        val btnSyllabus: TextView? = sideSheetDialog.findViewById(R.id.btnSyllabus)
        val btnExam: TextView? = sideSheetDialog.findViewById(R.id.btnExam)
        val btnMarks: TextView? = sideSheetDialog.findViewById(R.id.btnMarks)

        btnErpMenu.setOnClickListener {
            if (sideSheetDialog.isShowing) {
                sideSheetDialog.dismiss()
            } else {
                sideSheetDialog.show()
            }
        }

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
        erpViewModel.studentData.observe(viewLifecycleOwner) {
            if (it != null) {
                tvStuId?.text = it.studentID
                tvStuName?.text = it.studentName
            }
        }

        btnStudent?.setOnClickListener {
            sideSheetDialog.dismiss()
            Toast.makeText(requireContext(), "Student", Toast.LENGTH_SHORT).show()
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView2, erpViewModel.erpOptionStudent).commit()
        }

        btnAttendance?.setOnClickListener {
            sideSheetDialog.dismiss()
            Toast.makeText(requireContext(), "Attendance", Toast.LENGTH_SHORT).show()
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView2, ErpDefaultPage()).commit()
        }
    }

}