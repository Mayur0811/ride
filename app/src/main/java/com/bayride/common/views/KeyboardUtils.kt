package com.bayride.common.views

import android.app.Service
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View.hideKeyBoard() {
  (context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
    ?.hideSoftInputFromWindow(this.windowToken, 0)
}

fun View.showKeyboard() {
  (context?.getSystemService(Service.INPUT_METHOD_SERVICE) as? InputMethodManager)
    ?.showSoftInput(this, 0)
}