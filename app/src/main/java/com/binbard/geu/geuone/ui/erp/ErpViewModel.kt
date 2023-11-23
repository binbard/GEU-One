package com.binbard.geu.geuone.ui.erp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ErpViewModel: ViewModel() {
    private val _text: MutableLiveData<String> = MutableLiveData<String>().apply {
        value = "This is ERP Fragment"
    }
    val erpText: LiveData<String> = _text
}