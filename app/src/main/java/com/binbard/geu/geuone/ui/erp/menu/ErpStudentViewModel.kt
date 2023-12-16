package com.binbard.geu.geuone.ui.erp.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ErpStudentViewModel: ViewModel() {
    val details: MutableLiveData<String> = MutableLiveData<String>().apply {
        value = "Student[]"
    }

    val studentData = MutableLiveData<StudentData>().apply {
        value = null
    }

}