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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.binbard.geu.geuone.R
import com.binbard.geu.geuone.addMenuProvider
import com.binbard.geu.geuone.databinding.FragmentFeedBinding
import com.binbard.geu.geuone.models.LoginStatus
import com.binbard.geu.geuone.models.StatusCode
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FeedFragment: Fragment() {
    private lateinit var binding: FragmentFeedBinding
    private lateinit var fvm: FeedViewModel
    private lateinit var adapter: FeedRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)

        fvm = ViewModelProvider(requireActivity())[FeedViewModel::class.java]

        fvm.feedRepository = fvm.feedRepository ?: FeedRepository(AppDatabase.getInstance(requireActivity().application).feedDao())
        fvm.feedHelper = fvm.feedHelper ?: FeedHelper(fvm)

        binding.srlFeed.setProgressViewOffset(true, 50, 200)
        binding.srlFeed.setOnRefreshListener {
            fvm.feedHelper?.fetchData()
        }

        adapter = FeedRecyclerAdapter()
        binding.rvFeed.adapter = adapter
        binding.rvFeed.layoutManager = LinearLayoutManager(context)

        handleFeeds()
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

    private fun handleFeeds() {
        fvm.fetchStatus.observe(viewLifecycleOwner) {
            if (fvm.fetchStatus.value == StatusCode.NA) fvm.feedHelper?.fetchData()
            else if(it == StatusCode.SUCCESS){
                binding.pbFeed.visibility = View.GONE
                binding.srlFeed.isRefreshing = false
            }
        }
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
//        val toolbarFeed: Toolbar = requireActivity().findViewById(R.id.toolbarFeed)
//        val feedSearchView: SearchView = toolbarFeed.findViewById(R.id.feedSearchView)
//        feedSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                adapter.filter.filter(newText)
//                return true
//            }
//        })
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun setupFeeds(){
        GlobalScope.launch(Dispatchers.Main) {
//            val result = fvm.feedRepository?.getSearchFeedsPaginated("", 0, 10)
//            adapter.addFeeds(result ?: emptyList())
//            adapter.addFeeds(fvm.feedRepository?.getSomeFeeds() ?: emptyList())
        }
//        binding.rvFeed.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
//                val llm = rv.layoutManager as LinearLayoutManager
//                val visibleItemCount = llm.childCount
//                val totalItemCount = llm.itemCount
//                val firstVisibleItemPosition = llm.findFirstVisibleItemPosition()
//                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0) {
//                    GlobalScope.launch(Dispatchers.Main) {
//                        val result = fvm.feedRepository?.getSearchFeedsPaginated("", adapter.itemCount, 10)
//                        adapter.addFeeds(result ?: emptyList())
//                    }
//                }
//            }
//        })
    }

}

