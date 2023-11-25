package com.binbard.geu.geuone.ui.notes

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.geuone.databinding.FragmentNotesBinding
import com.binbard.geu.geuone.ui.feed.FeedFragment

class NotesFragment : Fragment() {
    private lateinit var binding: FragmentNotesBinding
    lateinit var notesViewModel: NotesViewModel
    lateinit var rvNotes: RecyclerView
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
        rvNotes.adapter = NotesRecyclerAdapter(requireContext(),notesViewModel.notes.value!!)

        val layoutManager = GridLayoutManager(context, 2)
        rvNotes.layoutManager = layoutManager

        notesViewModel.notes.observe(viewLifecycleOwner) {
            rvNotes.adapter?.notifyDataSetChanged()
        }

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d("POX", "Before handleOnBackPressed: "+notesViewModel.notes.value?.name)
                    val done = notesViewModel.gotoPrevDir()
                    Log.d("PXX", "After handleOnBackPressed: $done" +notesViewModel.notes.value?.name)

                    if (done) rvNotes.adapter?.notifyDataSetChanged()
//                    else requireActivity().onBackPressed()
                }
            }
            )


        return binding.root
    }

}
