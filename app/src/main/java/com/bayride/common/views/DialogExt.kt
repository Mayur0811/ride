package com.bayride.common.views

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import com.bayride.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder


fun Context.showAlertDialog(
    title: String,
    message: String,
    button: String,
    cancelable: Boolean = false,
    clickListener: ((Dialog) -> Unit)? = null
) {
    val dialog = MaterialAlertDialogBuilder(this)
        .setCancelable(cancelable)
        .setView(R.layout.common_alert_dialog)
        .create()

    dialog.show()

    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
    val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
    val btnOkay = dialog.findViewById<Button>(R.id.btnOk)

    tvTitle?.text = title
    tvMessage?.text = message
    btnOkay?.text = button

    btnOkay?.setOnClickListener {
        dialog.dismiss()
        clickListener?.invoke(dialog)
    }
}

fun Context.showConfirmDialog(
    title: String,
    message: String,
    button: String,
    secondButton: String? = null,
    cancelable: Boolean = true,
    clickListener: ((Dialog) -> Unit)? = null,
    secondClickListener: ((Dialog) -> Unit)? = null,
) {
    val dialog = MaterialAlertDialogBuilder(this)
        .setCancelable(cancelable)
        .setView(R.layout.common_alert_dialog)
        .create()

    dialog.show()

    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    val tvTitle = dialog.findViewById<TextView>(R.id.tvTitle)
    val tvMessage = dialog.findViewById<TextView>(R.id.tvMessage)
    val btnOkay = dialog.findViewById<Button>(R.id.btnOk)
    val btnLeave = dialog.findViewById<Button>(R.id.btnLeave)
    btnLeave?.visible(true)

    tvTitle?.text = title
    tvMessage?.text = message
    btnOkay?.text = button
    secondButton?.run {
        btnLeave?.text = this
    }

    btnOkay?.setOnClickListener {
        dialog.dismiss()
        clickListener?.invoke(dialog)
    }

    btnLeave?.setOnClickListener {
        dialog.dismiss()
        secondClickListener?.invoke(dialog)
    }
}

