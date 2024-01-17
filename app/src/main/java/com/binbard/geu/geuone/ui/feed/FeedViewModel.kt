package com.binbard.geu.geuone.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binbard.geu.geuone.models.FetchStatus
import com.binbard.geu.geuone.models.StatusCode

class FeedViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "The Feed"
    }
    val feedText: LiveData<String> = _text

    var comments = MutableLiveData<String>().apply {
        value = ""
    }

    val fetchStatus = MutableLiveData<FetchStatus>().apply {
        value = FetchStatus.NA
    }

    val feeds = mutableListOf<Feed>()

    var feedHelper: FeedHelper? = null
    var feedRepository: FeedRepository? = null

}