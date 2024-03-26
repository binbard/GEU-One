package com.binbard.geu.one.helpers

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object AlertMsg {
    fun showMessage(context: Context, title: String, message: String, okAction: () -> Unit = {}, repeat: Int = 0) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                okAction()
                if(repeat!=0) showMessage(context, title, message, okAction, repeat - 1)
            }
            .setCancelable(repeat>=0)
            .show()
    }
}