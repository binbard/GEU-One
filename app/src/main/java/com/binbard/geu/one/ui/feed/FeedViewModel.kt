package com.binbard.geu.one.ui.feed

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binbard.geu.one.models.FetchStatus

class FeedViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "The Feed"
    }

    var comments = MutableLiveData<String>().apply {
        value = ""
    }

    val fetchStatus = MutableLiveData<FetchStatus>().apply {
        value = FetchStatus.NA
    }

    var showAllFeeds = false

    val feeds = mutableListOf<Feed>()

    var feedRepository: FeedRepository? = null
    var feedHelper: FeedHelper? = null

}