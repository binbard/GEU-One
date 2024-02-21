package com.binbard.geu.one.ui.erp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.FragmentErpLoginChangeBinding
import com.binbard.geu.one.helpers.Snack
import com.binbard.geu.one.models.LoginStatus
import java.net.URL

class ErpLoginChangeFragment : Fragment() {
    private lateinit var binding: FragmentErpLoginChangeBinding
    private lateinit var evm: ErpViewModel
    private var link = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentErpLoginChangeBinding.inflate(inflater, container, false)

        evm = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        requireActivity().findViewById<TextView>(R.id.tvErpTitle).text = "ERP - Change Password"

        binding.tvChangePassSuccess.setTextColor(requireContext().resources.getColor(com.google.android.material.R.color.m3_dynamic_dark_highlighted_text))

        val uri = requireActivity().intent.data
        if (uri != null) {
            link = uri.toString()
        }

        evm.loginStatus.observe(viewLifecycleOwner) {
            if (it != LoginStatus.CHANGE_PASSWORD_SUCCESS && it != LoginStatus.CHANGE_PASSWORD_FAILED
                && it != LoginStatus.CHANGE_PASSWORD_EXPIRED) return@observe
            binding.pbErpPassChange.visibility = View.GONE
            binding.btChange.isEnabled = true

            if (it == LoginStatus.CHANGE_PASSWORD_FAILED) {
                Snack.showMsg(binding.root, "Could not change password.")
            } else {
                binding.etPass1.setText("")
                binding.etPass2.setText("")
                binding.etPass1.isEnabled = false
                binding.etPass2.isEnabled = false
                binding.tvChangePassSuccess.visibility = View.VISIBLE
                binding.btChange.text = "Go to Login"
                binding.btChange.setOnClickListener {
                    evm.loginStatus.value = LoginStatus.UNKNOWN
                    parentFragmentManager.popBackStack()
                }
                if(it == LoginStatus.CHANGE_PASSWORD_EXPIRED){
                    binding.tvChangePassSuccess.text = "The Reset Password Link is expired."
                }

            }

        }

        binding.btChange.setOnClickListener {
            val pass1 = binding.etPass1.text.toString()
            val pass2 = binding.etPass2.text.toString()

            if (pass1 != pass2) {
                binding.etPass2.error = "Passwords do not match."
                return@setOnClickListener
            }
            if (pass1.length < 8) {
                binding.etPass1.error = "Password must be at least 8 characters."
                return@setOnClickListener
            }

            val iim =
                requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            iim.hideSoftInputFromWindow(view?.windowToken, 0)

            binding.pbErpPassChange.visibility = View.VISIBLE
            binding.btChange.isEnabled = false
            evm.erpRepository?.changeErpPassword(evm, pass1, link)
        }

        return binding.root
    }
}