package com.binbard.geu.one.ui.res

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ResViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply{
        value = "Some Resources"
    }
    val resText: LiveData<String> = _text
}