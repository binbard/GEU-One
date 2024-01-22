package com.binbard.geu.one.ui.erp.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.databinding.FragmentErpMidtermMarksBinding
import com.binbard.geu.one.ui.erp.ErpViewModel

class ErpMidtermMarksFragment: Fragment() {
    private lateinit var binding: FragmentErpMidtermMarksBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErpMidtermMarksBinding.inflate(inflater, container, false)

        val evm: ErpViewModel = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        evm.midtermMarksData.observe(viewLifecycleOwner) {
            if(it==null){
//                evm.erpRepository?.fetchMidtermMarks(evm)
                return@observe
            }
//            binding.tvMidtermMarks.text = it.midtermMarks
//            binding.tvMidtermMaxMarks.text = it.maxMarks
//            binding.tvMidtermSubject.text = it.subject
//            binding.tvMidtermSubjectCode.text = it.subjectCode

        }

        return binding.root
    }
}