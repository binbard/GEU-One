package com.binbard.geu.one.ui.erp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.FragmentErpLoginResetBinding
import com.binbard.geu.one.helpers.AlertMsg
import com.binbard.geu.one.helpers.Snack
import com.binbard.geu.one.models.LoginStatus


class ErpLoginResetFragment : Fragment() {
    private lateinit var binding: FragmentErpLoginResetBinding
    private lateinit var evm: ErpViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErpLoginResetBinding.inflate(inflater, container, false)

        evm = ViewModelProvider(requireActivity())[ErpViewModel::class.java]

        requireActivity().findViewById<TextView>(R.id.tvErpTitle).text = "ERP - Reset Password"

        binding.etUid.setText(evm.loginId.value)

        binding.tvResetAvailable.setTextColor(requireContext().resources.getColor(com.google.android.material.R.color.m3_dynamic_dark_highlighted_text))

        evm.loginStatus.observe(viewLifecycleOwner) {
            if (it != LoginStatus.RESET_MATCH && it != LoginStatus.RESET_NOTMATCH) return@observe
            binding.pbErpPassReset.visibility = View.GONE
            binding.btReset.isEnabled = true

            if (it == LoginStatus.RESET_NOTMATCH) {
                Snack.showMsg(binding.root, "Check your details and try again.")
            } else {
                binding.etUid.setText("")
                binding.etEmail.setText("")
                binding.etDob.setText("")
                binding.etUid.isEnabled = false
                binding.etEmail.isEnabled = false
                binding.etDob.isEnabled = false
                binding.tvResetAvailable.visibility = View.VISIBLE
                binding.tvResetAvailable.text = "Reset Link sent on Registered Email ID"

                binding.btReset.text = "Open Mail App"
                binding.btReset.setOnClickListener {
                    val intent =
                        requireActivity().packageManager.getLaunchIntentForPackage("com.google.android.gm")
                    if (intent != null) startActivity(intent)
                    else Snack.showMsg(binding.root, "Mail App not Found")
                }
            }

        }

        binding.etDob.doOnTextChanged { text, start, before, count ->
            if (text != null && before < count) {
                if (text.length == 2) binding.etDob.setText("$text/")
                else if (text.length == 5) binding.etDob.setText("$text/")
                binding.etDob.text?.let { binding.etDob.setSelection(it.length) }
            }
        }

        binding.btReset.setOnClickListener {
            val id = binding.etUid.text.toString()
            val email = binding.etEmail.text.toString()
            val dob = binding.etDob.text.toString()

            if (id == "") binding.etUid.error = "Required"
            if (email == "") binding.etEmail.error = "Required"
            if (dob == "") binding.etDob.error = "Required"

            if (id != "" && email != "" && dob != "" && isAppLinkDefault()) {
                val iim =
                    requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                iim.hideSoftInputFromWindow(view?.windowToken, 0)

                binding.pbErpPassReset.visibility = View.VISIBLE
                binding.btReset.isEnabled = false
                evm.erpRepository?.resetErpPassword(evm, id, email, dob)
            }
            showAddLinkDialog()
        }

        return binding.root
    }

    fun isAppLinkDefault(): Boolean {
        val url1 = "https://student.geu.ac.in"
        val url2 = "https://student.gehu.ac.in"
        val intent1 = Intent(Intent.ACTION_VIEW, Uri.parse(url1))
        val intent2 = Intent(Intent.ACTION_VIEW, Uri.parse(url2))
        val pm = requireContext().packageManager
        return intent1.resolveActivity(pm) != null && intent2.resolveActivity(pm) != null
    }

    private fun showAddLinkDialog() {
        if(isAppLinkDefault()) return

        AlertMsg.showMessage(
            requireContext(),
            "Open by default",
            "Hey, It looks like you have not added App Links.\n\n" +
                    "Here is how you can do it:\n\n" +
                    "Open by default > Add link > [Tick all] > Add\n\n" +
                    "Then try again.",
            {
                if(isAppLinkDefault()) return@showMessage
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", context?.packageName, null)
                intent.data = uri
                startActivity(intent)
                showAddLinkDialog()
            }, repeat = 0
        )
    }
}