package com.binbard.geu.geuone.ui.res

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply{
        value = "This is res fragment"
    }
    val resText: LiveData<String> = _text
}