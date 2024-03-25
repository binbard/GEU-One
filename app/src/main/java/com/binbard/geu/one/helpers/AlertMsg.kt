package com.binbard.geu.one.helpers

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object AlertMsg {
    fun showMessage(context: Context, title: String, message: String, okAction: () -> Unit = {}, cancellable: Boolean = true) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                okAction()
                if(!cancellable) showMessage(context, title, message, okAction, cancellable)
            }
            .setCancelable(cancellable)
            .show()
    }
}