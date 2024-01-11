package com.binbard.geu.geuone.ui.erp.menu

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TableRow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.databinding.FragmentErpAttendanceBinding
import com.binbard.geu.geuone.ui.erp.ErpViewModel
import java.util.*


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


        if(evm.attendanceData.value==null){
            binding.tvAttendance.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }

        evm.attendanceData.observe(viewLifecycleOwner) {
            if(it == null){
                evm.erpRepository?.fetchAttendance(evm)
                return@observe
            }

            binding.progressBar.visibility = View.GONE
            binding.tvAttendance.visibility = View.VISIBLE

            val dateFrom = Helper.convertToHumanDate(it.totalAttendance.dateFrom)
            val dateTo = Helper.convertToHumanDate(it.totalAttendance.dateTo)
            val totalPercentage = it.totalAttendance.totalPercentage

            val txt = "Total Attendance: $totalPercentage%\nFrom $dateFrom to $dateTo"
            val spannableString = SpannableString(txt)
            spannableString.setSpan(android.text.style.RelativeSizeSpan(1.4f),0, txt.indexOf("\n"), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(android.text.style.UnderlineSpan(),0, txt.indexOf("\n"), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD),0, txt.indexOf("\n"), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(android.text.style.StyleSpan(android.graphics.Typeface.BOLD),txt.indexOf(":")+2, txt.indexOf(":")+2+totalPercentage.toString().length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

            binding.tvAttendance.text = spannableString

            binding.tblAttendance.removeAllViews()

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