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
    lateinit var nvm: NotesViewModel
    lateinit var rvNotes: RecyclerView
    lateinit var tvTitleNotes: TextView

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotesBinding.inflate(inflater, container, false)

        nvm = ViewModelProvider(this)[NotesViewModel::class.java]

        setHasOptionsMenu(true)

        if(nvm.notesRepository == null){
            nvm.notesRepository = NotesRepository(requireContext(),nvm)
            nvm.notesRepository!!.fetchData()
        }

        if(nvm.rvAdapter == null) nvm.rvAdapter = NotesRecyclerAdapter(requireContext(), nvm)
        rvNotes = binding.rvNotes
        rvNotes.adapter = nvm.rvAdapter

        rvNotes.addItemDecoration(ItemSpacingDecoration(10))
        val layoutManager = GridLayoutManager(context, 2)
        rvNotes.layoutManager = layoutManager

        nvm.notes.observe(viewLifecycleOwner) {
            rvNotes.adapter?.notifyDataSetChanged()
        }

        tvTitleNotes = requireActivity().findViewById(R.id.tvTitleNotes)

        nvm.notesTitle.observe(viewLifecycleOwner) {
            nvm.notesCacheHelper.setLastPath(it)
            tvTitleNotes.text = it
        }

        nvm.thumbDownloaded.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()) nvm.rvAdapter?.notifyDataSetChanged()
        }

        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id != null && id != -1L) {
                    val fileName = PdfUtils.removeDownloading(id)
                    if (fileName!=null) nvm.rvAdapter?.updateItem(fileName)
                }
            }
        }
        requireActivity().registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    nvm.gotoPrevDir()
                }
            })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_notes_top, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_notes_clearfiles -> {
                PdfUtils.clearAllFiles(requireContext())
                nvm.rvAdapter?.notifyDataSetChanged()
                Toast.makeText(requireActivity(), "Cleared Files", Toast.LENGTH_SHORT).show()
                true
            }
            else -> false
        }
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
