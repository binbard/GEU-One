package com.binbard.geu.one.ui.erp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binbard.geu.one.models.*
import com.binbard.geu.one.ui.erp.menu.Student

class ErpViewModel : ViewModel() {
    val comments: MutableLiveData<String> = MutableLiveData<String>().apply {
        value = ""
    }
    val loginStatus = MutableLiveData<LoginStatus>().apply {
        value = LoginStatus.UNKNOWN
    }
    val loginId = MutableLiveData<String>().apply {
        value = ""
    }
    val loginPass = MutableLiveData<String>().apply {
        value = ""
    }
    val erpStudentImg = MutableLiveData<String>().apply {
        value = ""
    }
    val studentData = MutableLiveData<Student>().apply {
        value = null
    }
    val attendanceData = MutableLiveData<Attendance>().apply {
        value = null
    }

    val midtermMarksData = MutableLiveData<MidtermMarksData>().apply {
        value = null
    }

    val examMarksData = MutableLiveData<ExamMarksData>()

    val currentErpPage = MutableLiveData<Int>().apply {
        value = 0
    }
    var shouldHandleInitPage = true
    var isCacheEnabled = true

    var erpCacheHelper: ErpCacheHelper? = null
    var erpRepository: ErpRepository? = null
}
