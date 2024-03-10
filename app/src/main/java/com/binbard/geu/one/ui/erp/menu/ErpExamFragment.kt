package com.binbard.geu.one.ui.erp.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.FragmentErpExamBinding
import com.binbard.geu.one.helpers.SharedPreferencesHelper
import com.binbard.geu.one.models.ExamMarks
import com.binbard.geu.one.ui.erp.ErpViewModel
import java.text.DecimalFormat
import kotlin.math.roundToInt

class ErpExamFragment: Fragment() {
    private lateinit var binding: FragmentErpExamBinding
    private val sharedPreferencesHelper: SharedPreferencesHelper by lazy {
        SharedPreferencesHelper(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErpExamBinding.inflate(inflater, container, false)

        val evm: ErpViewModel = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        if(evm.examMarksData.value==null) {
            binding.tvNoDataExam.visibility = View.INVISIBLE
            binding.tvExamCgpa.visibility = View.INVISIBLE
            evm.erpRepository?.fetchExamMarks(evm)
        }

        binding.srlExamMarks.setProgressViewOffset(false,50,200)
        binding.srlExamMarks.setOnRefreshListener {
            evm.erpRepository?.fetchExamMarks(evm)
            binding.tvExamCgpa.visibility = View.INVISIBLE
            binding.tblExamMarks.removeAllViews()
        }

        evm.examMarksData.observe(viewLifecycleOwner) {
            binding.srlExamMarks.isRefreshing = false
            binding.pbErpExam.visibility = View.GONE
            if(it==null || it.ExamSummary.isEmpty()){
                binding.tvNoDataExam.visibility = View.VISIBLE
                return@observe
            }
            binding.tvNoDataExam.visibility = View.GONE
            binding.tvExamCgpa.visibility = View.VISIBLE
            val marksList = it.ExamSummary

            binding.tblExamMarks.removeAllViews()
            binding.tblExamMarks.addView(Helper.getRowDividerBlack(requireContext()))

            val campus = sharedPreferencesHelper.getCampus()
            val erpHostUrl = if(campus == "deemed") getString(R.string.erpHostUrlDeemed) else getString(R.string.erpHostUrlDeemed)
            val regID = evm.studentData.value?.regID ?: ""
            val pRollNo = evm.studentData.value?.pRollNo ?: ""
            val cookies = evm.erpRepository?.cookies ?: ""

            val header = ExamMarks("Sem", "SGPA", "Back", "Result", "Marks", "Subject")
            val headerRow =  Helper.createExamMarksRow(requireContext(), 0, header, erpHostUrl, regID, pRollNo, cookies)
            binding.tblExamMarks.addView(headerRow)
            binding.tblExamMarks.addView(Helper.getRowDividerBlack(requireContext()))

            var cgpa = 0.0

            for (i in marksList.indices) {
                val row = Helper.createExamMarksRow(requireContext(), i+1, marksList[i], erpHostUrl, regID, pRollNo, cookies)
                binding.tblExamMarks.addView(row)
                binding.tblExamMarks.addView(Helper.getRowDividerBlack(requireContext()))
                cgpa += marksList[i].sgpa.toDouble()
            }

            cgpa /= marksList.size
            val df = DecimalFormat("#.00")
            val mCgpa = df.format((cgpa * 20).roundToInt() / 20.0)

            binding.tvExamCgpa.text = "CGPA (Agg.): $mCgpa"

            binding.tblExamMarks.visibility = View.VISIBLE
        }

        return binding.root
    }
}