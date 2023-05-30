package com.bayride.presentation.base

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.bayride.common.permission.Permission
import com.bayride.common.permission.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity(), DialogCommonView {
    private lateinit var permissionUtil: PermissionUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val language = SecureStorageManager(this).language
      //  changeLanguage(language)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        permissionUtil = PermissionUtil(this)
    }

    fun updateStatusBarColor(color: Int, isDark: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window?.let { window ->
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = color
                invertInsets(isDark, window)
            }
        }
    }

    @Suppress("DEPRECATION")
    fun invertInsets(darkTheme: Boolean, window: Window) {
        if (Build.VERSION.SDK_INT >= 30) {
            val statusBar = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            val navBar = WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            if (!darkTheme) {
                window.insetsController?.setSystemBarsAppearance(statusBar, statusBar)
                window.insetsController?.setSystemBarsAppearance(navBar, navBar)
            } else {
                window.insetsController?.setSystemBarsAppearance(0, statusBar)
                window.insetsController?.setSystemBarsAppearance(0, navBar)
            }
        } else {
            val flags = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    if (Build.VERSION.SDK_INT >= 26) View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR else 0

            if (!darkTheme) {
                window.decorView.systemUiVisibility =
                    window.decorView.systemUiVisibility or flags
            } else {
                window.decorView.systemUiVisibility =
                    (window.decorView.systemUiVisibility.inv() or flags).inv()
            }
        }
    }
    /**
     * Method to get layout xml id (e.g., R.layout.screen_abc)
     *
     * @return a layout xml id.
     */

    /**
     * Request grants permissions.
     *
     * @param permissions (variable number of arguments): List permission want to request
     * (e.g.,[Manifest.permission.ACCESS_COARSE_LOCATION], [Manifest.permission.READ_EXTERNAL_STORAGE],...)
     *
     * @param callback : Return result of request. There are two parameters:
     * areGrantedAll: True if all permissions are granted. False if at least one of those is declined.
     * deniedPermissions: List [Permission] request which are declined by user.
     */
    fun requestPermissions(
        vararg permissions: String,
        callback: (areGrantedAll: Boolean, deniedPermissions: List<Permission>) -> Unit
    ) {
        permissionUtil.request(*permissions, callback = callback)
    }

    override fun showSingleOptionDialog(
        title: String?,
        message: String,
        button: String,
        listener: DialogButtonClickListener?
    ): AlertDialog? {
        return buildDialog(title, message)
            .setPositiveButton(button) { dialog, _ ->
                listener?.invoke(dialog) ?: dialog.dismiss()
            }
            .show()
    }

    override fun showSingleOptionDialog(
        title: Int?,
        message: Int,
        button: Int,
        listener: DialogButtonClickListener?
    ): AlertDialog? {
        return showSingleOptionDialog(
            title = title?.let { getString(it) },
            message = getString(message),
            button = getString(button),
            listener = listener
        )
    }

    override fun showDoubleOptionsDialog(
        title: String?,
        message: String,
        firstButton: String,
        secondButton: String,
        firstButtonListener: DialogButtonClickListener?,
        secondButtonListener: DialogButtonClickListener?
    ): AlertDialog? {
        return buildDialog(title, message)
            .setPositiveButton(firstButton) { dialog, _ ->
                firstButtonListener?.invoke(dialog) ?: dialog.dismiss()
            }
            .setNegativeButton(secondButton) { dialog, _ ->
                secondButtonListener?.invoke(dialog) ?: dialog.dismiss()
            }
            .show()
    }

    override fun showDoubleOptionsDialog(
        title: Int?,
        message: Int,
        firstButton: Int,
        secondButton: Int,
        firstButtonListener: DialogButtonClickListener?,
        secondButtonListener: DialogButtonClickListener?
    ): AlertDialog? {
        return showDoubleOptionsDialog(
            title = title?.let { getString(it) },
            message = getString(message),
            firstButton = getString(firstButton),
            secondButton = getString(secondButton),
            firstButtonListener = firstButtonListener,
            secondButtonListener = secondButtonListener
        )
    }

    private fun buildDialog(title: String?, message: String): AlertDialog.Builder {
        return AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(title)
            .setMessage(message)
    }

    private fun changeLanguage(lang: String) {
        val config = resources.configuration
        val locale = Locale(lang)
        Locale.setDefault(locale)
        config.setLocale(locale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            createConfigurationContext(config)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

//    fun Context.changeLanguage(lang: String): Context {
//        val config = resources.configuration
//        val locale = Locale(lang)
//        Locale.setDefault(locale)
//        config.setLocale(locale)
//
//        val context =
//            createConfigurationContext(config)
////        else
////            resources.updateConfiguration(config, resources.displayMetrics)
//        return context
//    }
//
//    override fun attachBaseContext(newBase: Context?) {
//        val language = SecureStorageManager(newBase!!).language
//        super.attachBaseContext(newBase?.changeLanguage(language))
//    }

    override fun onResume() {
        super.onResume()
//        val language = SecureStorageManager(this).language
//        if (language != Locale.getDefault().language)
//            changeLanguage(language)
    }
}