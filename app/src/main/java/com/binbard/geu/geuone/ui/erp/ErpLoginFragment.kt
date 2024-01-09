package com.binbard.geu.geuone.ui.erp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.geuone.databinding.FragmentErpLoginBinding

class ErpLoginFragment : Fragment() {
    private lateinit var binding: FragmentErpLoginBinding
    private lateinit var evm: ErpViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErpLoginBinding.inflate(inflater, container, false)

        evm = ViewModelProvider(requireActivity())[ErpViewModel::class.java]


        binding.etId.doOnTextChanged { text, start, before, count ->
            evm.loginId.value = text.toString()
        }
        binding.etPass.doOnTextChanged { text, start, before, count ->
            evm.loginPass.value = text.toString()
        }

        binding.btLogin.setOnClickListener {

            val imm = requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)

            val id = binding.etId.text.toString()
            val pass = binding.etPass.text.toString()

            val erpCacheHelper = ErpCacheHelper(requireContext())
            if (id == "") {
                binding.etId.error = "Please enter your ID"
            }
            if (pass == "") {
                binding.etPass.error = "Please enter your password"
            }

            if (id != "" && pass != "") {
                erpCacheHelper.saveStudentId(id)
                erpCacheHelper.savePassword(pass)
                evm.erpRepository!!.preLogin(evm)
            }

        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.etId.setText(evm.loginId.value)
        binding.etPass.setText(evm.loginPass.value)
    }
}