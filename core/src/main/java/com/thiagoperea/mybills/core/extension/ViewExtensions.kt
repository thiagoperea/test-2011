package com.thiagoperea.mybills.core.extension

import android.content.Context
import android.view.View
import android.widget.Toast

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setHide() {
    this.visibility = View.GONE
}

fun Context.showToast(message: String?) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()