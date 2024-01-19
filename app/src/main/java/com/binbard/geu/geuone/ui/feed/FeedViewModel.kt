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

    var comments = MutableLiveData<String>().apply {
        value = ""
    }

    val fetchStatus = MutableLiveData<FetchStatus>().apply {
        value = FetchStatus.NA
    }

    val feeds = mutableListOf<Feed>()

    var feedRepository: FeedRepository? = null

}