package com.bayride.common.permission

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.bayride.R
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class PermissionUtil constructor(private val activity: FragmentActivity) {

    private val requestFragment: HandleResultFragment

    init {
        val fragment = activity.supportFragmentManager.findFragmentByTag(HandleResultFragment.TAG)
        if (fragment == null) {
            requestFragment = HandleResultFragment()
            activity
                .supportFragmentManager
                .beginTransaction()
                .add(requestFragment, HandleResultFragment.TAG)
                .commitAllowingStateLoss()
        } else {
            requestFragment = fragment as HandleResultFragment
        }
    }

    fun request(
        vararg permissions: String,
        callback: (areGrantedAll: Boolean, deniedPermissions: List<Permission>) -> Unit
    ) {
        val permissionSize = permissions.size
        val permissionsResult = arrayListOf<Permission>()

        val requestPermissions = arrayListOf<String>()
        permissions.forEach { permission ->
            if (isGranted(permission)) {
                permissionsResult.add(
                    Permission(
                        permission,
                        granted = true,
                        preventAskAgain = false
                    )
                )
            } else {
                var subject = requestFragment.getPermissionRequest(permission)
                if (subject == null) {
                    subject = fun(p: Permission) {
                        permissionsResult.add(p)
                        if (permissionsResult.size == permissionSize) {
                            val grantedAll = permissionsResult.all { it.granted }
                            if (grantedAll) {
                                callback.invoke(grantedAll, emptyList())
                            } else {
                                val permissionsNotGranted = permissionsResult.filter { !it.granted }
                                callback.invoke(grantedAll, permissionsNotGranted)
                            }
                        }
                    }
                }
                requestFragment.addPermissionRequest(permission, subject)
                requestPermissions.add(permission)
            }
        }

        if (requestPermissions.isNotEmpty() && !beforeAndroid6()) {
            requestFragment.request(requestPermissions.toTypedArray())
        } else {
            callback.invoke(true, emptyList())
        }
    }

    private fun isGranted(permission: String): Boolean {
        return activity.isPermissionsGranted(permission)
    }
}

fun Context.isPermissionsGranted(vararg permissions: String): Boolean {
    if (beforeAndroid6()) return true

    permissions.forEach { permission ->
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            return false
        }
    }
    return true
}

private fun beforeAndroid6(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.M

fun Context.showRationaleDialog(permissions: List<PermissionStatus>, request: PermissionRequest) {
    val msg = getString(
        R.string.rationale_permissions,
        permissions.toMessage<PermissionStatus.Denied.ShouldShowRationale>()
    )

    AlertDialog.Builder(this)
        .setTitle(R.string.permissions_required)
        .setMessage(msg)
        .setPositiveButton(R.string.request_again) { _, _ ->
            // Send the request again.
            request.send()
        }
        .setNegativeButton(android.R.string.cancel, null)
        .show()
}

fun Context.showPermanentlyDeniedDialog(permissions: List<PermissionStatus>) {
    val msg = getString(
        R.string.permanently_denied_permissions,
        permissions.toMessage<PermissionStatus.Denied.Permanently>()
    )

    AlertDialog.Builder(this)
        .setTitle(R.string.permissions_required)
        .setMessage(msg)
        .setPositiveButton(R.string.action_settings) { _, _ ->
            val intent = createAppSettingsIntent()
            startActivity(intent)
        }
        .setNegativeButton(android.R.string.cancel, null)
        .show()
}

private fun Context.createAppSettingsIntent() = Intent().apply {
    action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    data = Uri.fromParts("package", packageName, null)
}


private inline fun <reified T : PermissionStatus> List<PermissionStatus>.toMessage(): String =
    filterIsInstance<T>()
        .joinToString { it.permission }