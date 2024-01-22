package com.binbard.geu.one.ui.feed

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.core.view.MenuCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.one.R
import com.binbard.geu.one.databinding.FragmentFeedBinding
import com.binbard.geu.one.models.FetchStatus
import com.binbard.geu.one.ui.notes.NotesFragment
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

        setHasOptionsMenu(true)

        layoutManager = LinearLayoutManager(context)

        fvm.feedRepository = fvm.feedRepository ?: FeedRepository(
            AppDatabase.getInstance(
                requireActivity().application
            ).feedDao()
        )
        fvm.feedHelper = fvm.feedHelper ?: FeedHelper(requireContext())

        binding.srlFeed.setProgressViewOffset(true, 50, 200)
        binding.srlFeed.setOnRefreshListener {
            fvm.feedHelper!!.fetchData(fvm)
        }

        if(fvm.fetchStatus.value == FetchStatus.DONE) {
            binding.srlFeed.isRefreshing = false
            binding.pbFeed.visibility = View.GONE
        }

        fvm.fetchStatus.observe(viewLifecycleOwner) {
            if (fvm.fetchStatus.value == FetchStatus.NA){
                fvm.feedHelper!!.fetchData(fvm)
            } else if (it == FetchStatus.FAILED) {
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT).show()
            } else if (it == FetchStatus.NO_NEW_DATA_FOUND) {
                binding.srlFeed.isRefreshing = false
                binding.pbFeed.visibility = View.GONE
                fvm.fetchStatus.value = FetchStatus.DONE
            } else if (it == FetchStatus.NEW_DATA_FOUND) {
                binding.srlFeed.isRefreshing = false
                binding.pbFeed.visibility = View.GONE
                adapter.clearFeeds()
                addFeeds()
                fvm.fetchStatus.value = FetchStatus.DONE
            }
        }

        adapter = FeedRecyclerAdapter()
        binding.rvFeed.adapter = adapter
        binding.rvFeed.layoutManager = LinearLayoutManager(context)

        setupFeeds()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_feed_top, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_feed_top_site -> {
                val intent = CustomTabsIntent.Builder().build()
                intent.launchUrl(requireContext(), getString(R.string.feedsHostUrl).toUri())
                true
            }
            R.id.item_feed_show_only -> {
                item.isChecked = !fvm.showAllFeeds
                fvm.showAllFeeds = !fvm.showAllFeeds
                true
            }
            else -> false
        }
    }

    fun addFeeds(skip: Int = 0, limit: Int = 10) {
        val feedSearchView: SearchView = toolbarFeed.findViewById(R.id.feedSearchView)
        lifecycleScope.launch {
            val result = fvm.feedRepository?.getSearchFeedsPaginated(feedSearchView.query.toString() , skip, limit, fvm.showAllFeeds)
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

