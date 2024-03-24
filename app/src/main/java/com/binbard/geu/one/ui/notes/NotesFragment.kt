package com.binbard.geu.one.ui.notes

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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.DialogAddNotesBinding
import com.binbard.geu.one.databinding.DialogAddResourceBinding
import com.binbard.geu.one.databinding.FragmentNotesBinding
import com.binbard.geu.one.helpers.NetUtils
import com.binbard.geu.one.helpers.PdfUtils
import com.binbard.geu.one.helpers.Snack
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
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
            R.id.item_notes_upload -> {
                val dialogAddNotesBinding =
                    DialogAddNotesBinding.inflate(layoutInflater, null, false)

                val dsp = PreferenceManager.getDefaultSharedPreferences(requireContext())
                val sigName = dsp.getString("signature","Anonymous")

                dialogAddNotesBinding.etNotesAuthorName.setText(sigName)
                dialogAddNotesBinding.etNotesAuthorName.setOnClickListener {
                    Toast.makeText(requireContext(), "Change your signature in Settings", Toast.LENGTH_SHORT).show()
                }

                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Add Notes")
                    .setMessage("You can volunteer by providing Notes here. This will be sent for review.")
                    .setView(dialogAddNotesBinding.root)
                    .setNegativeButton("Cancel") { dialog, which ->
                        // Negative btn pressed
                    }
                    .setPositiveButton("ADD") { dialog, which ->
                        if(dialogAddNotesBinding.etNotesTitle.text.isEmpty() || dialogAddNotesBinding.etNotesUrl.text.isEmpty()) return@setPositiveButton
                        val feedbackUrl = resources.getString(R.string.feedbackUrl)
                        NetUtils.sendNotes(
                            requireContext(),
                            feedbackUrl,
                            "${dialogAddNotesBinding.etNotesTitle.text} | ${dialogAddNotesBinding.etNotesUrl.text}"
                        )
                    }
                    .show()
                true
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
