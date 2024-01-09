package com.binbard.geu.geuone.ui.erp.menu

import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.databinding.FragmentErpAttendanceBinding
import com.binbard.geu.geuone.models.SubjectAttendance
import com.binbard.geu.geuone.ui.erp.ErpViewModel
import java.text.SimpleDateFormat
import java.util.*
import javax.security.auth.Subject

class ErpAttendanceFragment: Fragment() {
    private lateinit var binding: FragmentErpAttendanceBinding
    private lateinit var evm: ErpViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErpAttendanceBinding.inflate(inflater, container, false)

        evm = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        evm.attendanceData.observe(viewLifecycleOwner) {
            if(it == null){
                evm.erpRepository?.fetchAttendance(evm)
                return@observe
            }
            val dateFrom = Helper.convertToHumanDate(it.totalAttendance.dateFrom)
            val dateTo = Helper.convertToHumanDate(it.totalAttendance.dateTo)
            val totalPercentage = it.totalAttendance.totalPercentage

            val txt = "Total Attendance: $totalPercentage%\n[$dateFrom - $dateTo]"
            val spannableString = SpannableString(txt)
            spannableString.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD),txt.indexOf(":")+2, txt.indexOf(":")+2+totalPercentage.toString().length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

            binding.tvAttendance.text = spannableString


            val attendanceList = it.subjectAttendance
            var count=0

            val separator = View(context)
            separator.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 1)
            separator.setBackgroundResource(com.google.android.material.R.color.material_divider_color)
            binding.tblAttendance.addView(separator)

            val header = Helper.createAttendanceRow(requireContext(),count, null)
            header.setBackgroundResource(com.google.android.material.R.color.material_divider_color)
            binding.tblAttendance.addView(header)

            for(attendance in attendanceList){
                val row = Helper.createAttendanceRow(requireContext(),++count, attendance)
                binding.tblAttendance.addView(row)
            }

        }

        return binding.root
    }

}