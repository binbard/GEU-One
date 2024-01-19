package com.binbard.geu.geuone.ui.feed

import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.addMenuProvider
import com.binbard.geu.geuone.databinding.FragmentFeedBinding
import com.binbard.geu.geuone.models.FetchStatus
import com.binbard.geu.geuone.models.LoginStatus
import com.binbard.geu.geuone.models.StatusCode
import com.binbard.geu.geuone.ui.notes.NotesFragment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {
    private lateinit var binding: FragmentFeedBinding
    private lateinit var fvm: FeedViewModel
    private lateinit var adapter: FeedRecyclerAdapter
    private lateinit var toolbarFeed: Toolbar
    private lateinit var layoutManager: LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)

        fvm = ViewModelProvider(requireActivity())[FeedViewModel::class.java]

        toolbarFeed = requireActivity().findViewById(R.id.toolbarFeed)
        layoutManager = LinearLayoutManager(context)

        fvm.feedRepository = fvm.feedRepository ?: FeedRepository(
            AppDatabase.getInstance(
                requireActivity().application
            ).feedDao()
        )

        binding.srlFeed.setProgressViewOffset(true, 50, 200)
        binding.srlFeed.setOnRefreshListener {
            FeedHelper.fetchData(fvm)
        }

        if(fvm.fetchStatus.value == FetchStatus.DONE) {
            binding.srlFeed.isRefreshing = false
            binding.pbFeed.visibility = View.GONE
        }

        fvm.fetchStatus.observe(viewLifecycleOwner) {
            if (fvm.fetchStatus.value == FetchStatus.NA){
                FeedHelper.fetchData(fvm)
            } else if (it == FetchStatus.FAILED) {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            } else if (it == FetchStatus.NO_NEW_DATA_FOUND) {
                binding.srlFeed.isRefreshing = false
                binding.pbFeed.visibility = View.GONE
                fvm.fetchStatus.value = FetchStatus.DONE
                Toast.makeText(requireContext(), "No new data found", Toast.LENGTH_SHORT).show()
            } else if (it == FetchStatus.NEW_DATA_FOUND) {
                binding.srlFeed.isRefreshing = false
                binding.pbFeed.visibility = View.GONE
                adapter.clearFeeds()
                addFeeds()
                fvm.fetchStatus.value = FetchStatus.DONE
                Toast.makeText(requireContext(), "new data found", Toast.LENGTH_SHORT).show()
            }
        }

        adapter = FeedRecyclerAdapter()
        binding.rvFeed.adapter = adapter
        binding.rvFeed.layoutManager = LinearLayoutManager(context)

        setupFeeds()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addMenuProvider(R.menu.menu_erp_top) {
            when (it) {
                R.id.item_erp_top_profile -> {
                    Toast.makeText(requireContext(), "Search", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    fun addFeeds(skip: Int = 0, limit: Int = 10) {
        val feedSearchView: SearchView = toolbarFeed.findViewById(R.id.feedSearchView)
        lifecycleScope.launch {
            val result = fvm.feedRepository?.getSearchFeedsPaginated(feedSearchView.query.toString() , skip, limit)
            if(skip==0) adapter.clearFeeds()
            adapter.addFeeds(result ?: emptyList())
        }
    }

    private fun setupFeeds() {
        val feedSearchView: SearchView = toolbarFeed.findViewById(R.id.feedSearchView)
        layoutManager = binding.rvFeed.layoutManager as LinearLayoutManager

        binding.rvFeed.addItemDecoration(NotesFragment.ItemSpacingDecoration(5))

        if(fvm.fetchStatus.value!=FetchStatus.DONE || feedSearchView.query!="") addFeeds()

        feedSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == null) return false
                addFeeds()
                return true
            }
        })

        binding.rvFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                val lastPosition = layoutManager.findLastVisibleItemPosition()
                if (lastPosition >= adapter.itemCount - 10) addFeeds(adapter.itemCount, 20)
            }
        })
    }

}

