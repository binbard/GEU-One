package com.binbard.geu.one.ui.erp

import android.os.Bundle
import android.util.Log
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
import com.binbard.geu.one.helpers.FirebaseUtils
import com.binbard.geu.one.helpers.SharedPreferencesHelper
import com.binbard.geu.one.models.LoginStatus
import com.binbard.geu.one.helpers.Snack
import com.binbard.geu.one.ui.erp.menu.*
import com.binbard.geu.one.utils.BitmapHelper
import com.google.android.material.navigation.NavigationView
import java.net.URL


class ErpFragment : Fragment() {
    private lateinit var binding: FragmentErpBinding
    private lateinit var evm: ErpViewModel
    private lateinit var tvErpTitle: TextView
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper
    private var firstTimeLogin = false
    private var campus = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentErpBinding.inflate(inflater, container, false)

        evm = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        setHasOptionsMenu(true)

        tvErpTitle = requireActivity().findViewById(R.id.tvErpTitle)

        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())

        campus = sharedPreferencesHelper.getCampus()

        if (evm.loginStatus.value == LoginStatus.LOGGED_IN) {
            setupErpFeatures()
            if (childFragmentManager.fragments.size == 0) showErpPage(R.id.item_erp_student)
        } else {
            showErpPage(0)
        }

        evm.loginStatus.observe(viewLifecycleOwner) {
            if (it == LoginStatus.UNKNOWN) {
                if (!handleInitPage()) evm.loginStatus.value = evm.erpCacheHelper?.getLoginStatus()
            } else if (it == LoginStatus.PREV_LOGGED_IN) {
                showErpPage(R.id.item_erp_student)
                evm.erpCacheHelper?.loadLocalStudentData(evm)
                evm.erpRepository?.preLogin(evm)
                setupErpFeatures()
            } else if (it == LoginStatus.PREV_LOGGED_OUT) {
                evm.loginStatus.value = LoginStatus.NOT_LOGGED_IN
                firstTimeLogin = true
                showErpPage(0)
            } else if (it == LoginStatus.LOGIN_SUCCESS) {
                evm.loginStatus.value = LoginStatus.LOGGED_IN
                setupErpFeatures()
                if (evm.currentErpPage.value == 0) showErpPage(R.id.item_erp_student)      // Redirect to student page
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
            if (it != "") {
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
                val url =
                    if (campus == "deemed") getString(R.string.erpHostUrl) else getString(R.string.erpHostUrlHill)
                intent.launchUrl(requireContext(), url.toUri())
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

    private fun handleInitPage(): Boolean {
        if (!evm.shouldHandleInitPage) return false
        val uri = requireActivity().intent.data ?: return false
        val url = URL(uri.scheme, uri.host, uri.path)
        val erpExtHost = resources.getString(R.string.erpExtHost)
        if (uri.host == erpExtHost && uri.path == "/Account/ChangePassword") {
            evm.shouldHandleInitPage = false
            showErpPage(R.id.ErpLoginChangeFragment)
            return true
        }
        return false
    }

    private fun showErpPage(pageId: Int) {
        evm.currentErpPage.value = pageId
        childFragmentManager.clearBackStack("xyz")
        val transaction = childFragmentManager.beginTransaction()
        when (pageId) {
            0 -> transaction.replace(R.id.fragmentContainerView2, ErpLoginFragment())
            R.id.ErpLoginChangeFragment -> {
                transaction.replace(R.id.fragmentContainerView2, ErpLoginChangeFragment())
                transaction.addToBackStack("xyz")
            }
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
            R.id.item_erp_exam -> {
                transaction.replace(R.id.fragmentContainerView2, ErpExamFragment())
                tvErpTitle.text = "ERP - Exam"
            }
            R.id.item_erp_fees -> {
                transaction.replace(R.id.fragmentContainerView2, ErpFeesFragment())
                tvErpTitle.text = "ERP - Fees"
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

        if (unset) {
            btnErpMenu.setOnClickListener(null)
            requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout).setDrawerLockMode(
                DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            )
            return
        }

        evm.studentData.observe(viewLifecycleOwner) {
            if (it == null) return@observe
            if (firstTimeLogin) {
                evm.erpCacheHelper?.saveSemester(it.yearSem)
                val mCourse = it.course.replace(" ", "_")
                FirebaseUtils.subscribeTo(mCourse)
                FirebaseUtils.subscribeTo("$campus.$mCourse")
                FirebaseUtils.subscribeTo("$campus.$mCourse.${it.yearSem}")
                firstTimeLogin = false
            } else {
                val savedSem = evm.erpCacheHelper?.getSemester()
                if (savedSem != it.yearSem) {
                    evm.erpCacheHelper?.saveSemester(it.yearSem)
                    val mCourse = it.course.replace(" ", "_")
                    FirebaseUtils.unsubscribeFrom("$campus.$mCourse.$savedSem")
                    FirebaseUtils.subscribeTo("$campus.$mCourse.${it.yearSem}")
                }
            }
            drawerLayout.findViewById<TextView>(R.id.tvStuId)?.text = it.studentID
            drawerLayout.findViewById<TextView>(R.id.tvStuName)?.text = it.studentName
        }

        requireActivity().findViewById<DrawerLayout>(R.id.drawer_layout).setDrawerLockMode(
            DrawerLayout.LOCK_MODE_UNLOCKED
        )

        btnErpMenu.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        drawerLayout.findViewById<NavigationView>(R.id.nav_view)
            .setNavigationItemSelectedListener { menuItem ->
                drawerLayout.closeDrawers()
                when (menuItem.itemId) {
                    R.id.item_erp_student -> showErpPage(R.id.item_erp_student)
                    R.id.item_erp_attendance -> showErpPage(R.id.item_erp_attendance)
                    R.id.item_erp_midterm_marks -> showErpPage(R.id.item_erp_midterm_marks)
                    R.id.item_erp_exam -> showErpPage(R.id.item_erp_exam)
                    R.id.item_erp_fees -> showErpPage(R.id.item_erp_fees)
                    else -> showErpPage(R.id.item_erp_student)
                }
                true
            }
    }

}