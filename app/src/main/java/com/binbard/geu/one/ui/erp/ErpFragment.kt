package com.binbard.geu.one.ui.erp

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.core.view.MenuCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.FragmentErpBinding
import com.binbard.geu.one.models.LoginStatus
import com.binbard.geu.one.ui.Snack
import com.binbard.geu.one.ui.erp.menu.ErpAttendanceFragment
import com.binbard.geu.one.ui.erp.menu.ErpMidtermMarksFragment
import com.binbard.geu.one.ui.erp.menu.ErpStudentFragment
import com.binbard.geu.one.utils.BitmapHelper
import com.google.android.material.navigation.NavigationView


class ErpFragment : Fragment(){
    private lateinit var binding: FragmentErpBinding
    private lateinit var evm: ErpViewModel
    private lateinit var tvErpTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentErpBinding.inflate(inflater, container, false)

        evm = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        setHasOptionsMenu(true)

        if(evm.loginStatus.value==LoginStatus.LOGGED_IN){
            setupErpFeatures()
            if(childFragmentManager.fragments.size==0) showErpPage(R.id.item_erp_student)
        } else {
            showErpPage(0)
        }

        tvErpTitle = requireActivity().findViewById(R.id.tvErpTitle)

        evm.loginStatus.observe(viewLifecycleOwner) {
            if (it == LoginStatus.UNKNOWN) {
                evm.loginStatus.value = evm.erpCacheHelper?.getLoginStatus()
            } else if (it == LoginStatus.PREV_LOGGED_IN) {
                showErpPage(R.id.item_erp_student)
                evm.erpCacheHelper?.loadLocalStudentData(evm)
                evm.erpRepository?.preLogin(evm)
                setupErpFeatures()
            } else if (it == LoginStatus.PREV_LOGGED_OUT) {
                evm.loginStatus.value = LoginStatus.NOT_LOGGED_IN
                showErpPage(0)
            } else if (it == LoginStatus.LOGIN_SUCCESS) {
                evm.loginStatus.value = LoginStatus.LOGGED_IN
                setupErpFeatures()
                if(evm.currentErpPage.value==0) showErpPage(R.id.item_erp_student)      // Redirect to student page
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
            if (it != ""){
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                evm.comments.value = ""
            }
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_erp_top, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_erp_top_visit_site -> {
                val intent = CustomTabsIntent.Builder().build()
                intent.launchUrl(requireContext(), getString(R.string.erpHostUrl).toUri())
                true
            }
            R.id.item_erp_top_cache_data -> {
                item.isChecked = !item.isChecked
                evm.isCacheEnabled = !evm.isCacheEnabled
                true
            }
            R.id.item_erp_top_logout -> {
                evm.loginStatus.value = LoginStatus.LOGOUT
                true
            }
            else -> false
        }
    }

    private fun showErpPage(pageId: Int) {
        evm.currentErpPage.value = pageId
        childFragmentManager.clearBackStack("xyz")
        val transaction = childFragmentManager.beginTransaction()
        when(pageId){
            0 -> transaction.replace(R.id.fragmentContainerView2, ErpLoginFragment())
            R.id.item_erp_student -> {
                transaction.replace(R.id.fragmentContainerView2, ErpStudentFragment())
                tvErpTitle.text = "ERP"
            }
            R.id.item_erp_attendance -> {
                transaction.replace(R.id.fragmentContainerView2, ErpAttendanceFragment())
                tvErpTitle.text = "ERP - Attendance"
            }
            R.id.item_erp_midterm_marks -> {
                transaction.replace(R.id.fragmentContainerView2, ErpMidtermMarksFragment())
                tvErpTitle.text = "ERP - Midterm Marks"
            }
            else -> {
                transaction.replace(R.id.fragmentContainerView2, ErpLoginFragment())
                tvErpTitle.text = "ERP"
            }
        }
        transaction.commit()
    }

    private fun setupErpFeatures(unset: Boolean = false) {
        val btnErpMenu: ImageView = requireActivity().findViewById(R.id.imgErpMenu)

        val drawerLayout: DrawerLayout = requireActivity().findViewById(R.id.drawer_layout)

        evm.erpStudentImg.observe(viewLifecycleOwner) {
            val bitmap = BitmapHelper.stringToBitmap(it)
            drawerLayout.findViewById<ImageView>(R.id.tvStuImg)?.setImageBitmap(bitmap)
        }
        evm.studentData.observe(viewLifecycleOwner) {
            drawerLayout.findViewById<TextView>(R.id.tvStuId)?.text = it?.studentID
            drawerLayout.findViewById<TextView>(R.id.tvStuName)?.text = it?.studentName
        }

        if (unset) {
            btnErpMenu.setOnClickListener(null)
            requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout).setDrawerLockMode(
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            return
        }

        requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout).setDrawerLockMode(
            DrawerLayout.LOCK_MODE_UNLOCKED)

        btnErpMenu.setOnClickListener{
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        drawerLayout.findViewById<NavigationView>(R.id.nav_view).setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawers()
            when (menuItem.itemId) {
                R.id.item_erp_student -> showErpPage(R.id.item_erp_student)
                R.id.item_erp_attendance -> showErpPage(R.id.item_erp_attendance)
                R.id.item_erp_midterm_marks -> showErpPage(R.id.item_erp_midterm_marks)
                else -> showErpPage(R.id.item_erp_student)
            }
            true
        }
    }

}