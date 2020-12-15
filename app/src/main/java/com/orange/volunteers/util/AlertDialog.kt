package com.orange.volunteers.util

import android.app.Activity
import android.text.Html
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

fun Activity.createAlertDialog(
    title: String?,
    message: String,
    positiveBtn: String?,
    negativeBtn: String?,
    neutralBtn: String? = null,
    callbackPos: (() -> Unit)? = null,
    callbackNeg: (() -> Unit)? = null,
    callbackNeutral: (() -> Unit)? = null,
    cancelable: Boolean = true
): AlertDialog {
    val alertDialogBuilder = AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(Html.fromHtml(message))
        .setCancelable(false)
        .setPositiveButton(positiveBtn) { dialog, _ ->
            callbackPos?.invoke()
            dialog.dismiss()
        }
        .setNegativeButton(negativeBtn) { dialog, _ ->
            callbackNeg?.invoke()
            dialog.dismiss()
        }
        .setNeutralButton(neutralBtn) { dialog, _ ->
            callbackNeutral?.invoke()
            dialog.dismiss()
        }
        .setCancelable(cancelable)

    val alertDialog: AlertDialog = alertDialogBuilder.create()
    if (alertDialog.getButton(AlertDialog.BUTTON_POSITIVE) != null) {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isAllCaps = false
    }
    if (alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE) != null) {
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).isAllCaps = false
    }
    if (alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL) != null) {
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).isAllCaps = false
    }
    return alertDialog
}

fun Fragment.createAlertDialog(
    title: String?,
    message: String,
    positiveBtn: String?,
    negativeBtn: String?,
    neutralBtn: String? = null,
    callbackPos: (() -> Unit)? = null,
    callbackNeg: (() -> Unit)? = null,
    callbackNeutral: (() -> Unit)? = null,
    cancelable: Boolean = true
): AlertDialog {
    return activity?.let {
        it.createAlertDialog(
            title,
            message,
            positiveBtn,
            negativeBtn,
            neutralBtn,
            callbackPos,
            callbackNeg,
            callbackNeutral,
            cancelable
        )
    } ?: error("Fragment not attached")
}