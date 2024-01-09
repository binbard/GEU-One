package com.binbard.geu.geuone.ui.erp

import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
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
import com.binbard.geu.geuone.models.LoginStatus
import com.binbard.geu.geuone.ui.Snack
import com.binbard.geu.geuone.ui.erp.menu.ErpAttendanceFragment
import com.binbard.geu.geuone.ui.erp.menu.ErpStudentFragment
import com.binbard.geu.geuone.utils.BitmapHelper
import com.google.android.material.sidesheet.SideSheetDialog


class ErpFragment : Fragment() {
    private lateinit var binding: FragmentErpBinding
    private lateinit var evm: ErpViewModel
    private lateinit var ssd: SideSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErpBinding.inflate(inflater, container, false)

        evm = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        if(evm.loginStatus.value==LoginStatus.LOGGED_IN){
            setupErpFeatures()
            if(childFragmentManager.fragments.size==0) showErpPage(R.id.btnStudent)
        }

        evm.loginStatus.observe(viewLifecycleOwner) {
            if (it == LoginStatus.UNKNOWN) {
                evm.loginStatus.value = evm.erpCacheHelper?.getLoginStatus()
            } else if (it == LoginStatus.PREV_LOGGED_IN) {
                showErpPage(R.id.btnStudent)
                evm.erpCacheHelper?.loadLocalStudentData(evm)
                evm.erpRepository?.preLogin(evm)
                setupErpFeatures()
            } else if (it == LoginStatus.PREV_LOGGED_OUT) {
                evm.loginStatus.value = LoginStatus.NOT_LOGGED_IN
                showErpPage(0)
                setupErpFeatures(unset = true)
            } else if (it == LoginStatus.LOGIN_SUCCESS) {
                evm.loginStatus.value = LoginStatus.LOGGED_IN
                if(evm.currentErpPage.value==0) showErpPage(R.id.btnStudent)                // Redirect to student page
                evm.erpCacheHelper!!.saveLoginStatus(LoginStatus.PREV_LOGGED_IN)
                evm.erpRepository?.syncStudentData(evm)
            } else if (it == LoginStatus.LOGIN_FAILED) {
                if (evm.erpCacheHelper?.getLoginStatus() == LoginStatus.PREV_LOGGED_IN) {
                    Snack.showMsg(
                        requireActivity().findViewById(android.R.id.content),
                        "Session Expired"
                    )
                    evm.erpCacheHelper?.saveLoginStatus(LoginStatus.PREV_LOGGED_OUT)
                } else {
                    Snack.showMsg(
                        requireActivity().findViewById(android.R.id.content),
                        "Wrong Credentials"
                    )
                }
                setupErpFeatures(unset = true)
                evm.loginStatus.value = LoginStatus.NOT_LOGGED_IN
            } else if (it == LoginStatus.LOGOUT) {
                Toast.makeText(requireContext(), "Logged Out", Toast.LENGTH_SHORT).show()
                evm.erpCacheHelper!!.saveLoginStatus(LoginStatus.PREV_LOGGED_OUT)
                evm.erpCacheHelper!!.clearLocalData()
                setupErpFeatures(unset = true)
                showErpPage(0)
                evm.loginStatus.value = LoginStatus.NOT_LOGGED_IN
            }
        }

        evm.comments.observe(viewLifecycleOwner) {
            if (it != "") Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun showErpPage(pageId: Int) {
        evm.currentErpPage.value = pageId
        childFragmentManager.clearBackStack("xyz")
        val transaction = childFragmentManager.beginTransaction()
        if(pageId==0){
            transaction.replace(R.id.fragmentContainerView2, ErpLoginFragment())
        } else if(pageId==R.id.btnStudent){
            transaction.replace(R.id.fragmentContainerView2, ErpStudentFragment())
        } else if(pageId==R.id.btnAttendance){
            transaction.replace(R.id.fragmentContainerView2, ErpAttendanceFragment())
        }
        transaction.commit()
    }

    private fun setupErpFeatures(unset: Boolean = false) {
        val btnErpMenu: ImageView = requireActivity().findViewById(R.id.imgErpMenu)

        if (unset) {
            btnErpMenu.setOnClickListener(null)
            return
        }

        ssd = SideSheetDialog(requireContext())
        ssd.setContentView(R.layout.fragment_erp_sidesheet)
        ssd.setSheetEdge(Gravity.START)

        btnErpMenu.setOnClickListener {
            if (ssd.isShowing) ssd.dismiss()
            else ssd.show()
        }

        val btnStudent: TextView? = ssd.findViewById(R.id.btnStudent)
        val btnAttendance: TextView? = ssd.findViewById(R.id.btnAttendance)
        val btnTimetable: TextView? = ssd.findViewById(R.id.btnTimetable)
        val btnSyllabus: TextView? = ssd.findViewById(R.id.btnSyllabus)
        val btnExam: TextView? = ssd.findViewById(R.id.btnExam)
        val btnMarks: TextView? = ssd.findViewById(R.id.btnMarks)

        btnStudent?.setOnClickListener {
            showErpPage(R.id.btnStudent)
            ssd.dismiss()
        }
        btnAttendance?.setOnClickListener {
            showErpPage(R.id.btnAttendance)
            ssd.dismiss()
        }

        evm.erpStudentImg.observe(viewLifecycleOwner) {
            val bitmap = BitmapHelper.stringToBitmap(it)
            ssd.findViewById<ImageView>(R.id.tvStuImg)?.setImageBitmap(bitmap)
        }
        evm.studentData.observe(viewLifecycleOwner) {
            ssd.findViewById<TextView>(R.id.tvStuId)?.text = it?.studentID
            ssd.findViewById<TextView>(R.id.tvStuName)?.text = it?.studentName
        }
    }

}