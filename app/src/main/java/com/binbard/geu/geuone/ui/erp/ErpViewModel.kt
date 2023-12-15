package com.binbard.geu.geuone.ui.erp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ErpViewModel: ViewModel() {
    private val _text: MutableLiveData<String> = MutableLiveData<String>().apply {
        value = "Your ERP"
    }
    val loginDone: MutableLiveData<Boolean> = MutableLiveData<Boolean>().apply {
        value = false
    }
    val erpText: LiveData<String> = _text
    val erpId = MutableLiveData<String>().apply {
        value = ""
    }
    val erpPassword = MutableLiveData<String>().apply {
        value = ""
    }

}
