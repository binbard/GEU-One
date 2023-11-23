package com.binbard.geu.geuone.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.binbard.geu.geuone.databinding.FragmentNotesBinding

class NotesFragment: Fragment() {
    private lateinit var binding: FragmentNotesBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotesBinding.inflate(inflater, container, false)

        val notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)
        notesViewModel.notesText.observe(viewLifecycleOwner) {
            binding.textNotes.text = it
        }

        return binding.root
    }
}