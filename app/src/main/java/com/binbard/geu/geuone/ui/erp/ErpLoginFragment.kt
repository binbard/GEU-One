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
    private lateinit var erpViewModel: ErpViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentErpLoginBinding.inflate(inflater, container, false)

        erpViewModel = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        val btLogin = binding.btLogin
        val etId = binding.etId
        val etPass = binding.etPass

        etId.doOnTextChanged { text, start, before, count ->
            erpViewModel.erpStudentId.value = text.toString()
        }

        etPass.doOnTextChanged { text, start, before, count ->
            erpViewModel.erpPassword.value = text.toString()
        }

        btLogin.setOnClickListener {

            val imm = requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken, 0)

            val id = etId.text.toString()
            val pass = etPass.text.toString()
            val erpCacheHelper = ErpCacheHelper(requireContext())
            if (id == "") {
                etId.error = "Please enter your ID"
            }
            if (pass == "") {
                etPass.error = "Please enter your password"
            }

            if (id != "" && pass != "") {
                erpCacheHelper.saveStudentId(id)
                erpCacheHelper.savePassword(pass)
                erpViewModel.erpRepository!!.preLogin(erpViewModel)
            }

        }

        return binding.root
    }
}