package com.binbard.geu.geuone.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binbard.geu.geuone.models.StatusCode

class FeedViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "The Feed"
    }
    val feedText: LiveData<String> = _text

    var comments = MutableLiveData<String>().apply {
        value = ""
    }

    val fetchStatus = MutableLiveData<StatusCode>().apply {
        value = StatusCode.NA
    }

    val feedList = MutableLiveData<List<Feed>>()

    var feedHelper: FeedHelper? = null

}