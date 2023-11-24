package com.binbard.geu.geuone.ui.feed

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.geuone.databinding.FragmentFeedBinding

class FeedFragment: Fragment() {
    private lateinit var binding: FragmentFeedBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)

        val feedViewModel = ViewModelProvider(this)[FeedViewModel::class.java]
        val rvFeed = binding.rvFeed

        rvFeed.addItemDecoration(ItemSpacingDecoration(10))
        rvFeed.adapter = FeedRecyclerAdapter(feedViewModel.feedList.value ?: emptyList())
        rvFeed.layoutManager = LinearLayoutManager(context)

        feedViewModel.feedList.observe(viewLifecycleOwner) {
            rvFeed.adapter = FeedRecyclerAdapter(it)
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

}

