package com.binbard.geu.one.ui.erp

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.FragmentErpLoginBinding
import com.binbard.geu.one.models.LoginStatus

class ErpLoginFragment : Fragment() {
    private lateinit var binding: FragmentErpLoginBinding
    private lateinit var evm: ErpViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErpLoginBinding.inflate(inflater, container, false)

        evm = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        requireActivity().findViewById<TextView>(R.id.tvErpTitle).text = "ERP - Login"

        if(evm.loginStatus.value == LoginStatus.RESET_MATCH || evm.loginStatus.value == LoginStatus.RESET_NOTMATCH){
            evm.loginStatus.value = LoginStatus.UNKNOWN
        }

        val spannable = SpannableString("Forgot Password?")
        spannable.setSpan(URLSpan(null), 0, spannable.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvForgotPass.text = spannable
        binding.tvForgotPass.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(
                R.id.fragmentContainerView2,
                ErpLoginResetFragment()
            ).addToBackStack(null).commit()
        }

        binding.etId.doOnTextChanged { text, start, before, count ->
            evm.loginId.value = text.toString()
        }
        binding.etPass.doOnTextChanged { text, start, before, count ->
            evm.loginPass.value = text.toString()
        }

        binding.btLogin.setOnClickListener {

            val imm =
                requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
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
                binding.btLogin.isEnabled = false
                binding.etId.isEnabled = false
                binding.etPass.isEnabled = false
                binding.pbErpLogin.visibility = View.VISIBLE
            }
        }

        evm.loginStatus.observe(viewLifecycleOwner) {
            if (it == LoginStatus.NOT_LOGGED_IN) {
                binding.btLogin.isEnabled = true
                binding.etId.isEnabled = true
                binding.etPass.isEnabled = true
                binding.pbErpLogin.visibility = View.GONE
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