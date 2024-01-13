package com.binbard.geu.geuone.ui.feed

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.databinding.FragmentFeedBinding
import com.binbard.geu.geuone.models.StatusCode

class FeedFragment: Fragment() {
    private lateinit var binding: FragmentFeedBinding
    private lateinit var fvm: FeedViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)

        fvm = ViewModelProvider(requireActivity())[FeedViewModel::class.java]

        if(fvm.feedHelper == null){
            fvm.feedHelper = FeedHelper(requireActivity().application, fvm)
        }

        val fetchRemote = true

        if(fvm.feedList.value == null){
            fvm.feedHelper?.fetchData(fetchRemote)
        }

        fvm.fetchStatus.observe(viewLifecycleOwner) {
            if(it == StatusCode.SUCCESS){
                binding.pbFeed.visibility = View.GONE
            }
        }

        binding.rvFeed.addItemDecoration(ItemSpacingDecoration(10))
        binding.rvFeed.adapter = FeedRecyclerAdapter(fvm.feedList.value ?: emptyList())
        binding.rvFeed.layoutManager = LinearLayoutManager(context)

        fvm.feedList.observe(viewLifecycleOwner) {
            val adapter = FeedRecyclerAdapter(it)
            setupToolbar(adapter)
            binding.rvFeed.adapter = adapter
        }

        fvm.comments.observe(viewLifecycleOwner) {
            if(it.isNotEmpty()){
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                fvm.comments.value = ""
            }
        }

        return binding.root
    }

    class ItemSpacingDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            outRect.left = space
            outRect.right = space
            outRect.top = space
            outRect.bottom = space
        }
    }

    private fun setupToolbar(adapter: FeedRecyclerAdapter){
        val toolbarFeed: Toolbar = requireActivity().findViewById(R.id.toolbarFeed)
        val feedSearchView: SearchView = toolbarFeed.findViewById(R.id.feedSearchView)
        feedSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })
    }

}

