package com.binbard.geu.geuone.ui.erp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ErpViewModel: ViewModel() {
    val loginStatus = MutableLiveData<Int>().apply {
        value = -1
    }
    val erpStudentId = MutableLiveData<String>().apply {
        value = ""
    }
    val erpPassword = MutableLiveData<String>().apply {
        value = ""
    }
    val erpStudentName = MutableLiveData<String>().apply {
        value = ""
    }
    val erpStudentImg = MutableLiveData<String>().apply {
        value = ""
    }

}
