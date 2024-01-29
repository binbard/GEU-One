package com.binbard.geu.one.ui.erp.menu

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.databinding.FragmentErpMidtermMarksBinding
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
                binding.tvTemp.visibility = View.VISIBLE
                binding.lvMidtermMarks.adapter = null
                return@observe
            }
            binding.tvTemp.visibility = View.GONE
            val marksList = it.state
            val marksAdapter = ArrayAdapter(requireContext(), R.layout.simple_list_item_1, marksList)
            binding.lvMidtermMarks.adapter = marksAdapter
        }

        return binding.root
    }
}