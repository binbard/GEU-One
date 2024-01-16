package com.binbard.geu.geuone.ui.erp.menu

import android.os.Bundle
import android.text.SpannableString
import android.view.*
import android.widget.TableRow
import android.widget.Toast
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.databinding.FragmentErpAttendanceBinding
import com.binbard.geu.geuone.ui.erp.ErpViewModel
import com.google.android.material.navigation.NavigationView
import java.util.*


class ErpAttendanceFragment : Fragment() {
    private lateinit var binding: FragmentErpAttendanceBinding
    private lateinit var evm: ErpViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErpAttendanceBinding.inflate(inflater, container, false)

        evm = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        if(!evm.isCacheEnabled){
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
            separator.setBackgroundResource(com.google.android.material.R.color.material_divider_color)
            binding.tblAttendance.addView(separator)

            val header = Helper.createAttendanceRow(requireContext(), count, null)
            header.setBackgroundResource(com.google.android.material.R.color.material_divider_color)
            binding.tblAttendance.addView(header)

            for (attendance in it.subjectAttendance) {
                val row = Helper.createAttendanceRow(requireContext(), ++count, attendance)
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
            android.text.style.ForegroundColorSpan(resources.getColor(com.google.android.material.R.color.m3_dynamic_dark_highlighted_text)),
            txt.indexOf("\n"),
            txt.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            android.text.style.ForegroundColorSpan(resources.getColor(com.google.android.material.R.color.m3_dynamic_dark_highlighted_text)),
            0,
            txt.indexOf("\n"),
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
            0,
            txt.indexOf("\n"),
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableString.setSpan(
            android.text.style.ForegroundColorSpan(resources.getColor(com.google.android.material.R.color.m3_ref_palette_dynamic_neutral70)),
            txt.indexOf("\n"),
            txt.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

}