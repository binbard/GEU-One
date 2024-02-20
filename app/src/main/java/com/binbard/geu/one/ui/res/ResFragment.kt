package com.binbard.geu.one.ui.res

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.MenuCompat
import androidx.core.view.marginStart
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.FragmentResBinding
import com.binbard.geu.one.databinding.ItemResCardBinding
import com.binbard.geu.one.helpers.PdfUtils
import com.binbard.geu.one.ui.erp.ErpCacheHelper
import com.binbard.geu.one.ui.erp.ErpViewModel
import com.binbard.geu.one.ui.erp.menu.Student
import com.binbard.geu.one.ui.notes.NotesFragment
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*


class ResFragment : Fragment() {
    private lateinit var binding: FragmentResBinding
    private lateinit var rvm: ResViewModel
    private lateinit var evm: ErpViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResBinding.inflate(inflater, container, false)

        rvm = ViewModelProvider(this)[ResViewModel::class.java]

        evm = ViewModelProvider(this)[ErpViewModel::class.java]
        evm.erpCacheHelper = evm.erpCacheHelper ?: ErpCacheHelper(requireContext())

        setHasOptionsMenu(true)

        val intent = CustomTabsIntent.Builder().build()

        rvm.resRepository = rvm.resRepository ?: ResRepository(requireContext())

        rvm.resList.observe(viewLifecycleOwner) {
            if (it == null) {
                rvm.resRepository?.fetchResources(rvm)
                return@observe
            }

            if (evm.studentData.value == null) evm.erpCacheHelper?.loadLocalStudentData(evm)

            binding.llResBox.removeAllViews()
            for (resSection in it) {
                val resTitle = resSection.title
                val resObjList = resSection.content

                if(resTitle=="Events"){
                    if(resObjList.isEmpty()) continue
                    val vpRes = ViewPager2(requireContext())
                    val params = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        220
                    )
                    vpRes.layoutParams = params
                    val events = mutableListOf<Pair<String,String>>()
                    for(resObj in resObjList){
                        events.add(Pair(resObj.imgUrl?:"",resObj.url))
                    }
                    vpRes.visibility = View.VISIBLE
                    vpRes.adapter = CarouselAdapter(requireContext(), events)
                    CoroutineScope(Dispatchers.Main).launch {
                        while (isActive) {
                            delay(8000)
                            vpRes.currentItem = (vpRes.currentItem + 1) % events.size
                        }
                    }
                    binding.llResBox.addView(vpRes)
                    continue
                }

                val filteredList = resObjList.filter { shouldShowResObj(evm.studentData.value, it.onlyFor) }

                val resCardBinding = ItemResCardBinding.inflate(inflater, container, false)
                resCardBinding.tvResCardTitle.text = resTitle
                resCardBinding.rvResCardBody.layoutManager = GridLayoutManager(context, 3)
                resCardBinding.rvResCardBody.adapter = GridAdapter(filteredList)

                binding.llResBox.addView(resCardBinding.root)

            }
        }

        return binding.root
    }

    private fun shouldShowResObj(student: Student?, onlyFor: String?): Boolean {
        if (onlyFor == null) return true
        if (student == null) return false
        val course = student.course
        val specialization = student.courseSpecialization
        val yearSem = student.yearSem

        val iCourse = onlyFor.substringBefore('#').substringBefore('@')
        val iSpecialization =
            if (onlyFor.contains('@')) onlyFor.substringAfter('@').substringBefore('#') else null
        val iYearSem = if (onlyFor.contains('#')) onlyFor.substringAfter('#') else null

        if (iCourse != course) return false
        if (iSpecialization != null && iSpecialization != specialization) return false
        if (iYearSem != null && iYearSem != yearSem) return false
        return true
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