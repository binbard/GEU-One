package com.binbard.geu.geuone.ui.erp.menu

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.databinding.FragmentErpStudentBinding
import com.binbard.geu.geuone.ui.erp.ErpCacheHelper
import com.binbard.geu.geuone.ui.erp.ErpRepository
import com.google.android.material.sidesheet.SideSheetDialog

class ErpStudentFragment: Fragment() {
    private lateinit var binding: FragmentErpStudentBinding
    private lateinit var sideSheetDialog: SideSheetDialog
    private lateinit var erpCacheHelper: ErpCacheHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentErpStudentBinding.inflate(inflater, container, false)

        val erpStudentViewModel = ViewModelProvider(this)[ErpStudentViewModel::class.java]

        erpStudentViewModel.details.observe(viewLifecycleOwner) {
            binding.tvErpStudent.text = it
        }

        erpCacheHelper = ErpCacheHelper(requireContext())

        sideSheetDialog = SideSheetDialog(requireContext())
        val tvStuName: TextView? = sideSheetDialog.findViewById(R.id.tvStuName)
        val tvStuId: TextView? = sideSheetDialog.findViewById(R.id.tvStuId)

        erpStudentViewModel.studentData.observe(viewLifecycleOwner) {
            if(it!=null) {
                binding.tblStudentDetails.visibility = View.VISIBLE

                erpCacheHelper.saveStudentName(it.studentName)
                erpCacheHelper.saveStudentId(it.studentID)

                tvStuName?.text = it.studentName
                tvStuId?.text = it.studentID

                it.properties.forEach{(name, value) ->
                    val row = TableRow(context)
                    row.layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.MATCH_PARENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                    )

                    val textViewName = TextView(context)
                    textViewName.textSize = 16f
                    textViewName.text = name
                    textViewName.gravity = Gravity.START

                    val params = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                    params.setMargins(0, 0, 20, 0)
                    textViewName.layoutParams = params

                    val textViewValue = TextView(context)
                    textViewValue.textSize = 16f
                    textViewValue.text = value

                    row.addView(textViewName)
                    row.addView(textViewValue)

                    binding.tblStudentDetails.addView(row)
                }
            }
        }

        val erpCacheHelper = ErpCacheHelper(requireContext())
        val erpRepository = ErpRepository(erpCacheHelper)

        erpRepository.getStudentDetails(erpStudentViewModel)


        return binding.root
    }
}