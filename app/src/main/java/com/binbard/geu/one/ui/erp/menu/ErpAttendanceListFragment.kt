package com.binbard.geu.one.ui.erp.menu

import android.os.Bundle
import android.text.SpannableString
import android.util.TypedValue
import android.view.*
import android.widget.Toast
import android.widget.TableRow
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainer
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.FragmentErpAttendanceBinding
import com.binbard.geu.one.databinding.FragmentErpAttendanceListBinding
import com.binbard.geu.one.models.LoginStatus
import com.binbard.geu.one.ui.erp.ErpViewModel
import java.util.*


class ErpAttendanceListFragment : Fragment() {
    private lateinit var binding: FragmentErpAttendanceListBinding
    private lateinit var evm: ErpViewModel
    private var selectedRow = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErpAttendanceListBinding.inflate(inflater, container, false)

        evm = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        if (!evm.isCacheEnabled) {
//            Toast.makeText(requireContext(), "Cache Disabled", Toast.LENGTH_SHORT).show()
            evm.attendanceData.value = null
        }

        if (evm.attendanceData.value == null) {
            binding.tvAttendance.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }

        evm.attendanceData.observe(viewLifecycleOwner) {
            if (it == null) {
                evm.erpRepository?.fetchAttendance(evm)
                return@observe
            }

            binding.progressBar.visibility = View.GONE
            binding.tvAttendance.visibility = View.VISIBLE

            val dateFrom = Helper.convertToHumanDate(it.totalAttendance.dateFrom)
            val dateTo = Helper.convertToHumanDate(it.totalAttendance.dateTo)
            val totalPercentage = it.totalAttendance.totalPercentage

            val txt = "Total Attendance: $totalPercentage%\nFrom $dateFrom to $dateTo"

            binding.tvAttendance.text = getSpannableAttendanceTitle(txt)

            binding.tblAttendance.removeAllViews()

            var count = 0
            val separator = View(context)
            separator.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1)
            try{
                separator.setBackgroundResource(com.google.android.material.R.color.material_divider_color)
            } catch (e: Exception){
                separator.setBackgroundColor(resources.getColor(com.google.android.material.R.color.material_divider_color))
            }
            binding.tblAttendance.addView(separator)

            val header = Helper.createAttendanceRow(requireContext(), count, null)
            try{
                header.setBackgroundResource(com.google.android.material.R.color.material_divider_color)
            } catch (e: Exception){
                header.setBackgroundColor(resources.getColor(com.google.android.material.R.color.material_divider_color))
            }
            binding.tblAttendance.addView(header)

            val subjectAttendance = it.subjectAttendance
            val totalAttendance = it.totalAttendance

            binding.btnAtDetails.setOnClickListener {
                if(selectedRow == -1) return@setOnClickListener
                val attendance = subjectAttendance[selectedRow]
                val fragment = ErpAttendanceDetailsFragment()
                val bundle = Bundle()

                if(evm.studentData.value?.regID == null) {
                    if(evm.loginStatus.value==LoginStatus.PREV_LOGGED_IN){
                        Toast.makeText(requireContext(), "Please wait", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
                    }
                    return@setOnClickListener
                }
                bundle.putString("regID", evm.studentData.value?.regID)
                bundle.putString("subjectID", attendance.subjectId)
                bundle.putString("periodAssignID", attendance.periodAssignId)
                bundle.putString("ttid", attendance.ttid)
                bundle.putString("lectureTypeID", attendance.lectureTypeId)
                bundle.putSerializable("dateFrom", totalAttendance.dateFrom)
                bundle.putSerializable("dateTo", totalAttendance.dateTo)
                bundle.putString("employee", attendance.employee)

                bundle.putString("SubjectCode", attendance.subjectCode)
                bundle.putString("Subject", attendance.subject)
                bundle.putString("Employee", attendance.employee)
                bundle.putString("TotalLecture", attendance.totalLecture)
                bundle.putString("TotalPresent", attendance.totalPresent)
                bundle.putString("Percentage", attendance.percentage)

                evm.subjectAttendanceData.value = null
                fragment.arguments = bundle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView4, fragment)
                    .addToBackStack(null)
                    .commit()
            }

            for (i in it.subjectAttendance.indices) {
                val attendance = it.subjectAttendance[i]
                val row = Helper.createAttendanceRow(requireContext(), ++count, attendance)
                row.setOnClickListener {
                    if (selectedRow == i) {
                        row.setBackgroundResource(0)
                        selectedRow = -1
                        binding.btnAtDetails.visibility = View.GONE
                    } else {
                        binding.btnAtDetails.visibility = View.GONE
                        try{
                            row.setBackgroundResource(com.google.android.material.R.color.material_divider_color)
                        } catch (e: Exception){
                            row.setBackgroundColor(resources.getColor(com.google.android.material.R.color.material_divider_color))
                        }
                        binding.btnAtDetails.postDelayed({
                            binding.btnAtDetails.visibility = View.VISIBLE
                        }, 800)
                        if (selectedRow != -1) {
                            val prevRow =
                                binding.tblAttendance.getChildAt(selectedRow + 2) as TableRow
                            prevRow.setBackgroundResource(0)
                        }
                    }
                    selectedRow = i
                }
                binding.tblAttendance.addView(row)
            }

        }

        return binding.root
    }

    private fun getSpannableAttendanceTitle(txt: String): SpannableString {
        val spannableString = SpannableString(txt)

        spannableString.setSpan(
            android.text.style.RelativeSizeSpan(1.4f),
            0,
            txt.indexOf("\n"),
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            android.text.style.UnderlineSpan(),
            txt.indexOf(":") + 2,
            txt.indexOf("\n") - 1,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.ITALIC),
            txt.indexOf("\n"),
            txt.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            0,
            txt.indexOf("\n"),
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

}