package com.binbard.geu.one.ui.erp.menu

import android.R
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.databinding.FragmentErpMidtermMarksBinding
import com.binbard.geu.one.models.MidtermMarks
import com.binbard.geu.one.ui.erp.ErpViewModel
import java.lang.Integer.max

class ErpMidtermMarksFragment: Fragment() {
    private lateinit var binding: FragmentErpMidtermMarksBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErpMidtermMarksBinding.inflate(inflater, container, false)

        val evm: ErpViewModel = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        if(evm.studentData.value!=null){
            val yearSem = evm.studentData.value?.yearSem!!.toInt()
            if(yearSem>0){
                val semList = 1..yearSem
                val spAdapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, semList.toList())
                spAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                binding.spMidTermSemester.adapter = spAdapter
                binding.spMidTermSemester.setSelection(max( 0, yearSem-2))
            }
        }

        binding.spMidTermSemester.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                evm.erpRepository?.fetchMidtermMarks(evm, position+1)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        evm.midtermMarksData.observe(viewLifecycleOwner) {
            if(it==null || it.state.isEmpty()){
                binding.tvNoDataMidterm.visibility = View.VISIBLE
                binding.tblMidtermMarks.visibility = View.GONE
                return@observe
            }
            binding.tvNoDataMidterm.visibility = View.GONE
            val marksList = it.state

            binding.tblMidtermMarks.removeAllViews()
            val separator = View(context)
            separator.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5)
            separator.setBackgroundResource(R.color.black)
            binding.tblMidtermMarks.addView(separator)

            val header = MidtermMarks("Marks", "#/Subject", "0", "Max Marks")
            val headerRow =  Helper.createMidtermMarksRow(requireContext(), 0, header)
            binding.tblMidtermMarks.addView(headerRow)

            for (i in marksList.indices) {
                val row = Helper.createMidtermMarksRow(requireContext(), i+1, marksList[i])
                binding.tblMidtermMarks.addView(row)

                val sep = View(context)
                sep.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 5)
                sep.setBackgroundResource(R.color.black)
                binding.tblMidtermMarks.addView(sep)
            }

            binding.tblMidtermMarks.visibility = View.VISIBLE
        }

        return binding.root
    }
}