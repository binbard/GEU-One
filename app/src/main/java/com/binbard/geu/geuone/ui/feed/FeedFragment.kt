package com.binbard.geu.geuone.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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

        feedViewModel.feedText.observe(viewLifecycleOwner) {
            binding.textFeed.text = it
        }

//        val mList = MutableLiveData<List<Feed>>(
//            List(10) { Feed("Loading...", "Hii") }
//        )
//        val dList: LiveData<List<Feed>> = mList
        rvFeed.adapter = FeedRecyclerAdapter(feedViewModel.feedList.value ?: emptyList())
        rvFeed.layoutManager = LinearLayoutManager(context)

        feedViewModel.feedList.observe(viewLifecycleOwner) {
            rvFeed.adapter = FeedRecyclerAdapter(it)
        }

        return binding.root
    }
}