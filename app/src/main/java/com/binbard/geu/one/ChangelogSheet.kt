package com.binbard.geu.one

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import com.binbard.geu.one.databinding.ChangelogBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class ChangelogSheet: BottomSheetDialogFragment() {
    private val binding by lazy { ChangelogBottomSheetBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val notes = listOf(
            "Fixed Login Issue in New Devices.",
            "IDCard can now be Downloaded.",
            "Midsem Marks defaults to current semester.",
            "Added Email based support for Users.",
            "Upload Notes by sending via Email.",
            "Removed forced App Links acceptance."
        )

        binding.llChanges.removeAllViews()
        notes.forEachIndexed { index, note ->
            addChangeNote("${index + 1}. $note")
        }

        binding.imClose.setOnClickListener {
            dismiss()
        }
        return binding.root
    }

    private fun addChangeNote(note: String){
        val textView = TextView(context)
        textView.text = note
        textView.textSize = 20f
        textView.setPadding(0, 0, 0, 20)
        binding.llChanges.addView(textView)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener {

            val bottomSheetDialog = it as BottomSheetDialog
            val parentLayout =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            parentLayout?.let { it ->
                val behaviour = BottomSheetBehavior.from(it)
                setupFullHeight(it)
                behaviour.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

}