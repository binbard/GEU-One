package com.binbard.geu.geuone.ui.notes

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.addMenuProvider
import com.binbard.geu.geuone.databinding.FragmentNotesBinding
import com.binbard.geu.geuone.ui.feed.FeedFragment

class NotesFragment : Fragment() {
    private lateinit var binding: FragmentNotesBinding
    lateinit var notesViewModel: NotesViewModel
    lateinit var rvNotes: RecyclerView
    private var titleListener: FragmentTitleListener? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotesBinding.inflate(inflater, container, false)

        notesViewModel = ViewModelProvider(this)[NotesViewModel::class.java]
        rvNotes = binding.rvNotes

        rvNotes.addItemDecoration(FeedFragment.ItemSpacingDecoration(10))
        rvNotes.adapter = NotesRecyclerAdapter(requireContext(), notesViewModel)

        val layoutManager = GridLayoutManager(context, 2)
        rvNotes.layoutManager = layoutManager

        notesViewModel.notes.observe(viewLifecycleOwner) {
            rvNotes.adapter?.notifyDataSetChanged()
        }

        val notesCacheHelper = NotesCacheHelper(requireContext())

        notesViewModel.notesTitle.observe(viewLifecycleOwner) {
            titleListener?.updateTitle(it)
            notesCacheHelper.setLastPath(it)
        }

        val tvTitleNotes: TextView = requireActivity().findViewById(R.id.tvTitleNotes)

        notesViewModel.notesTitle.observe(viewLifecycleOwner) {
            tvTitleNotes.text = it
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    notesViewModel.gotoPrevDir()
                }
            }
            )

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is FragmentTitleListener) {
            titleListener = context
        } else {
            throw ClassCastException("$context must implement FragmentTitleListener")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMenuProvider(R.menu.menu_erp_top) {
            when (it) {
                R.id.item_res_top_check-> {
                    Toast.makeText(requireContext(), "Notes Upload", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.item_res_top_feedback -> {
                    true
                }
                R.id.item_res_top_clearfiles -> {
                    PdfUtils.clearAllFiles(requireContext())
                    Toast.makeText(requireContext(), "Cleared All Notes", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

}
