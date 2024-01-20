package com.binbard.geu.geuone.ui.notes

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.databinding.FragmentNotesBinding

class NotesFragment : Fragment() {
    private lateinit var binding: FragmentNotesBinding
    lateinit var notesViewModel: NotesViewModel
    lateinit var rvNotes: RecyclerView
    lateinit var tvTitleNotes: TextView

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesBinding.inflate(inflater, container, false)

        notesViewModel = ViewModelProvider(this)[NotesViewModel::class.java]

        if(notesViewModel.rvAdapter == null) notesViewModel.rvAdapter = NotesRecyclerAdapter(requireContext(), notesViewModel)
        rvNotes = binding.rvNotes
        rvNotes.adapter = notesViewModel.rvAdapter

        rvNotes.addItemDecoration(ItemSpacingDecoration(10))
        val layoutManager = GridLayoutManager(context, 2)
        rvNotes.layoutManager = layoutManager

        notesViewModel.notes.observe(viewLifecycleOwner) {
            rvNotes.adapter?.notifyDataSetChanged()
        }

        tvTitleNotes = requireActivity().findViewById(R.id.tvTitleNotes)

        notesViewModel.notesTitle.observe(viewLifecycleOwner) {
            notesViewModel.notesCacheHelper.setLastPath(it)
            tvTitleNotes.text = it
        }

        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id != null && id != -1L) {
                    val fileName = PdfUtils.removeDownloading(id)
                    if (fileName!=null) notesViewModel.rvAdapter?.notifyDataSetChanged()
                }
            }
        }
        requireActivity().registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    notesViewModel.gotoPrevDir()
                }
            })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
    }

    private fun setupToolbar(){
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_notes_top, menu)
                MenuCompat.setGroupDividerEnabled(menu, true)
            }
            @SuppressLint("NotifyDataSetChanged")
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.item_res_clearfiles -> {
                        PdfUtils.clearAllFiles(requireContext())
                        notesViewModel.rvAdapter!!.notifyDataSetChanged()
                        Toast.makeText(requireContext(), "Cleared Files", Toast.LENGTH_SHORT).show()
                        true
                    }
                    else -> false
                }
            }
        })
    }

    class ItemSpacingDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.left = space
            outRect.right = space
            outRect.top = space
            outRect.bottom = space
        }
    }

}
