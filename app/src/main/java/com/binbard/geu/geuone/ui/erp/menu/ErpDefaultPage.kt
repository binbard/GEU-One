package com.binbard.geu.geuone.ui.erp.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ErpDefaultPage: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(com.binbard.geu.geuone.R.layout.fragment_erp_default_page, container, false)
        return view
    }
}