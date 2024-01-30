package com.binbard.geu.one.ui.erp.menu

import com.binbard.geu.one.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.databinding.FragmentErpExamBinding
import com.binbard.geu.one.models.ExamMarks
import com.binbard.geu.one.ui.erp.ErpViewModel

class ErpExamFragment: Fragment() {
    private lateinit var binding: FragmentErpExamBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErpExamBinding.inflate(inflater, container, false)

        val evm: ErpViewModel = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        if(evm.examMarksData.value==null) {
            evm.erpRepository?.fetchExamMarks(evm)
        }

        binding.srlExamMarks.setProgressViewOffset(true, 50, 200)
        binding.srlExamMarks.setOnRefreshListener {
            evm.erpRepository?.fetchExamMarks(evm)
            binding.tblExamMarks.removeAllViews()
        }

        evm.examMarksData.observe(viewLifecycleOwner) {
            binding.srlExamMarks.isRefreshing = false
            if(it==null || it.ExamSummary.isEmpty()){
                binding.tvNoDataExam.visibility = View.VISIBLE
                return@observe
            }
            binding.tvNoDataExam.visibility = View.GONE
            val marksList = it.ExamSummary

            binding.tblExamMarks.removeAllViews()
            binding.tblExamMarks.addView(Helper.getRowDividerBlack(requireContext()))

            val erpHostUrl = getString(R.string.erpHostUrl)
            val regID = evm.studentData.value?.regID ?: ""
            val cookies = evm.erpRepository?.cookies ?: ""

            val header = ExamMarks("Sem", "SGPA", "Back", "Result", "Marks", "Subject")
            val headerRow =  Helper.createExamMarksRow(requireContext(), 0, header, erpHostUrl, regID, cookies)
            binding.tblExamMarks.addView(headerRow)
            binding.tblExamMarks.addView(Helper.getRowDividerBlack(requireContext()))

            for (i in marksList.indices) {
                val row = Helper.createExamMarksRow(requireContext(), i+1, marksList[i], erpHostUrl, regID, cookies)
                binding.tblExamMarks.addView(row)
                binding.tblExamMarks.addView(Helper.getRowDividerBlack(requireContext()))
            }

            binding.tblExamMarks.visibility = View.VISIBLE
        }

        return binding.root
    }
}