package com.binbard.geu.geuone.ui.erp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.binbard.geu.geuone.models.Attendance
import com.binbard.geu.geuone.models.LoginStatus
import com.binbard.geu.geuone.models.MidtermMarks
import com.binbard.geu.geuone.ui.erp.menu.Student

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

    val midtermMarksData:  MutableLiveData<MidtermMarks> = MutableLiveData<MidtermMarks>().apply {
        value = null
    }

    val currentErpPage = MutableLiveData<Int>().apply {
        value = 0
    }
    var isCacheEnabled = true

    var erpCacheHelper: ErpCacheHelper? = null
    var erpRepository: ErpRepository? = null
}
