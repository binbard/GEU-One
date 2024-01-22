package com.binbard.geu.one.ui

import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar

object Snack {
    fun showMsg(viewGroup: ViewGroup, message: String) {
        val snackbar = Snackbar.make(viewGroup, message, Snackbar.LENGTH_SHORT)
        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(20, 0, 20, 180)
        snackbarView.layoutParams = params
        snackbar.setAction("OK") {
            snackbar.dismiss()
        }
        snackbar.show()
    }
}