package com.binbard.geu.geuone.ui.erp

import android.os.Bundle
import android.text.TextUtils.replace
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.geuone.R
import androidx.fragment.app.Fragment
import com.binbard.geu.geuone.databinding.FragmentErpBinding
import com.binbard.geu.geuone.models.ErpPage
import com.binbard.geu.geuone.models.LoginStatus
import com.binbard.geu.geuone.ui.Snack
import com.binbard.geu.geuone.ui.erp.menu.ErpAttendanceFragment
import com.binbard.geu.geuone.ui.erp.menu.ErpStudentFragment
import com.binbard.geu.geuone.utils.BitmapHelper
import com.google.android.material.navigation.NavigationView
import com.google.android.material.sidesheet.SideSheetDialog


class ErpFragment : Fragment() {
    private lateinit var binding: FragmentErpBinding
    private lateinit var evm: ErpViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErpBinding.inflate(inflater, container, false)

        evm = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        if(evm.loginStatus.value==LoginStatus.LOGGED_IN){
            setupErpFeatures()
            if(childFragmentManager.fragments.size==0) showErpPage(ErpPage.STUDENT)
        }

        evm.loginStatus.observe(viewLifecycleOwner) {
            if (it == LoginStatus.UNKNOWN) {
                evm.loginStatus.value = evm.erpCacheHelper?.getLoginStatus()
            } else if (it == LoginStatus.PREV_LOGGED_IN) {
                showErpPage(ErpPage.STUDENT)
                evm.erpCacheHelper?.loadLocalStudentData(evm)
                evm.erpRepository?.preLogin(evm)
                setupErpFeatures()
            } else if (it == LoginStatus.PREV_LOGGED_OUT) {
                evm.loginStatus.value = LoginStatus.NOT_LOGGED_IN
                showErpPage(ErpPage.LOGIN)
                setupErpFeatures(unset = true)
            } else if (it == LoginStatus.LOGIN_SUCCESS) {
                evm.loginStatus.value = LoginStatus.LOGGED_IN
                if(evm.currentErpPage.value==ErpPage.LOGIN) showErpPage(ErpPage.STUDENT)                // Redirect to student page
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
                showErpPage(ErpPage.LOGIN)
                evm.loginStatus.value = LoginStatus.NOT_LOGGED_IN
            }
        }

        evm.comments.observe(viewLifecycleOwner) {
            if (it != "") Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun showErpPage(erpPage: ErpPage) {
        evm.currentErpPage.value = erpPage
        childFragmentManager.clearBackStack("xyz")
        val transaction = childFragmentManager.beginTransaction()
        when(erpPage){
            ErpPage.LOGIN -> transaction.replace(R.id.fragmentContainerView2, ErpLoginFragment())
            ErpPage.STUDENT -> transaction.replace(R.id.fragmentContainerView2, ErpStudentFragment())
            ErpPage.ATTENDANCE -> transaction.replace(R.id.fragmentContainerView2, ErpAttendanceFragment())
            else -> transaction.replace(R.id.fragmentContainerView2, ErpLoginFragment())
        }
        transaction.commit()
    }

    private fun setupErpFeatures(unset: Boolean = false) {
        val btnErpMenu: ImageView = requireActivity().findViewById(R.id.imgErpMenu)

        val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)

        drawerLayout.findViewById<NavigationView>(R.id.nav_view).setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawers()
            when (menuItem.itemId) {
                R.id.item_erp_student -> showErpPage(ErpPage.STUDENT)
                R.id.item_erp_attendance -> showErpPage(ErpPage.ATTENDANCE)
                R.id.item_erp_timetable -> showErpPage(ErpPage.STUDENT)
                else -> showErpPage(ErpPage.STUDENT)
            }
            true
        }

        btnErpMenu.setOnClickListener{
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        if (unset) {
            btnErpMenu.setOnClickListener(null)
            requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout).setDrawerLockMode(
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            return
        }

        evm.erpStudentImg.observe(viewLifecycleOwner) {
            val bitmap = BitmapHelper.stringToBitmap(it)
            drawerLayout.findViewById<ImageView>(R.id.tvStuImg)?.setImageBitmap(bitmap)
        }
        evm.studentData.observe(viewLifecycleOwner) {
            drawerLayout.findViewById<TextView>(R.id.tvStuId)?.text = it?.studentID
            drawerLayout.findViewById<TextView>(R.id.tvStuName)?.text = it?.studentName
        }
    }

}