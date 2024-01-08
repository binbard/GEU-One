package com.binbard.geu.geuone.ui.erp

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
 import com.binbard.geu.geuone.models.LoginStatus
import com.binbard.geu.geuone.ui.Snack
import com.binbard.geu.geuone.ui.erp.menu.ErpAttendanceFragment
 import com.binbard.geu.geuone.utils.BitmapHelper
import com.google.android.material.sidesheet.SideSheetDialog


class ErpFragment : Fragment() {
    private lateinit var binding: FragmentErpBinding
    private lateinit var sideSheetDialog: SideSheetDialog
    private lateinit var evm: ErpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErpBinding.inflate(inflater, container, false)

        evm = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        evm.loginStatus.observe(viewLifecycleOwner) {
            if(it== LoginStatus.UNKNOWN){
                evm.loginStatus.value = evm.erpCacheHelper?.getLoginStatus()
            }
            else if(it==LoginStatus.PREV_LOGGED_IN){
                setupErpFeatures()
                evm.erpCacheHelper?.loadLocalStudentData(evm)
                showErpPage(evm.erpOptionStudent)
                evm.erpRepository?.preLogin(evm)
            }
            else if(it== LoginStatus.PREV_LOGGED_OUT){
                evm.loginStatus.value = LoginStatus.NOT_LOGGED_IN
                showErpPage(ErpLoginFragment())
            }
            else if(it==LoginStatus.LOGIN_SUCCESS){
                evm.loginStatus.value = LoginStatus.LOGGED_IN
                evm.erpCacheHelper!!.saveLoginStatus(LoginStatus.PREV_LOGGED_IN)
                setupErpFeatures()
                showErpPage(evm.erpOptionStudent)
                evm.erpRepository?.syncStudentData(evm)
            }
            else if(it==LoginStatus.LOGIN_FAILED){
                evm.loginStatus.value = LoginStatus.NOT_LOGGED_IN
                setupErpFeatures(unset=true)
                if(evm.erpCacheHelper?.getLoginStatus()==LoginStatus.PREV_LOGGED_IN){
                    Snack.showMsg(requireActivity().findViewById(android.R.id.content), "Session Expired")
//                    evm.erpCacheHelper?.saveLoginStatus(LoginStatus.PREV_LOGGED_OUT)
                    showErpPage(ErpLoginFragment())
                } else{
                    Snack.showMsg(requireActivity().findViewById(android.R.id.content), "Wrong Credentials")
                }
            }
            else if(it==LoginStatus.LOGOUT){
                Toast.makeText(requireContext(), "Logged Out", Toast.LENGTH_SHORT).show()
                setupErpFeatures(unset=true)
                evm.erpCacheHelper!!.saveLoginStatus(LoginStatus.PREV_LOGGED_OUT)
                evm.erpCacheHelper!!.clearLocalData()
                evm.loginStatus.value = LoginStatus.NOT_LOGGED_IN
                showErpPage(ErpLoginFragment())
            }
        }

        return binding.root
    }

    private fun showErpPage(erpStudentFragment: Fragment){
        childFragmentManager.popBackStack()
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView2, erpStudentFragment).commit()
    }

    private fun setupErpFeatures(unset: Boolean = false) {

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

        evm.erpStudentImg.observe(viewLifecycleOwner) {
            val bitmap = BitmapHelper.stringToBitmap(it)
            tvStuImg?.setImageBitmap(bitmap)
        }
        evm.studentData.observe(viewLifecycleOwner) {
            tvStuId?.text = it?.studentID
            tvStuName?.text = it?.studentName
            if (it != null) {
                tvStuId?.text = it.studentID
                tvStuName?.text = it.studentName
            }
        }

        btnStudent?.setOnClickListener {
            sideSheetDialog.dismiss()
            Toast.makeText(requireContext(), "Student", Toast.LENGTH_SHORT).show()
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView2, evm.erpOptionStudent).commit()
        }

        btnAttendance?.setOnClickListener {
            sideSheetDialog.dismiss()
            Toast.makeText(requireContext(), "Attendance", Toast.LENGTH_SHORT).show()
            childFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView2, ErpAttendanceFragment()).commit()
        }
    }

}