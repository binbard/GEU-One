package com.binbard.geu.geuone.ui.erp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binbard.geu.geuone.ui.erp.menu.ErpStudentFragment
import com.binbard.geu.geuone.ui.erp.menu.Student

class ErpViewModel: ViewModel() {
    val comments: MutableLiveData<String> = MutableLiveData<String>().apply {
        value = "Something"
    }
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
    val studentData = MutableLiveData<Student>().apply {
        value = null
    }

    val erpOptionStudent = ErpStudentFragment()

    var erpCacheHelper: ErpCacheHelper? = null
    var erpRepository: ErpRepository? = null
}
