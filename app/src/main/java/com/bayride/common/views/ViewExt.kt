package com.bayride.common.views

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.renderscript.Allocation
import androidx.renderscript.Element
import androidx.renderscript.RenderScript
import androidx.renderscript.ScriptIntrinsicBlur
import com.bayride.R
import com.bayride.common.sharedpreference.getSavedObjectFromPreference
import com.bayride.common.utils.Constants
import com.bayride.data.datasources.remote.entities.ProfileEntity
import com.bayride.data.datasources.remote.entities.SigUpResponse
import com.bayride.data.datasources.remote.entities.SignInResponse
import com.bayride.data.models.dto.Driver
import com.bayride.data.models.dto.PassengerSignup
import java.util.*
import java.util.regex.Pattern

fun View.visible(show: Boolean) {
    this.visibility = if (show) View.VISIBLE else View.GONE
}

fun View.toInvisible() {
    this.visibility = View.INVISIBLE
}

fun Context.dpToPx(dp: Int): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

fun Drawable.convertDrawableToBitmap(expectWidth: Int? = null, expectHeight: Int? = null): Bitmap {
    val width = expectWidth ?: intrinsicWidth
    val height = expectHeight ?: intrinsicHeight
    val drawableBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(drawableBitmap)
    setBounds(0, 0, width, height)
    draw(canvas)
    return drawableBitmap
}

fun Activity.getScreenWidth(): Int {
    return if (Build.VERSION.SDK_INT < 30) {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    } else {
        val metrics = windowManager.currentWindowMetrics
        val insets = metrics.windowInsets
            .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        metrics.bounds.width() - insets.left - insets.right
    }
}

fun ImageView.setTint(@ColorRes colorRes: Int) {
    ImageViewCompat.setImageTintList(
        this,
        ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
    )
}

fun Context.showDialog(
    activity: Activity,
    title: String,
    positiveButtonFunction: (() -> Unit)? = null,
    negativeButtonFunction: (() -> Unit)? = null
) {
    val decorView: View? = activity?.window?.decorView
    val rootView = decorView?.findViewById<View>(android.R.id.content) as ViewGroup
    val windowBackground = decorView.background

    val dialog = Dialog(this)
    dialog.window?.requestFeature(Window.FEATURE_NO_TITLE) // if you have blue line on top of your dialog, you need use this code
    dialog.window?.setBackgroundDrawable(resources.getDrawable(R.drawable.rounded_dialog_rectengle))
    dialog.setCancelable(false)
    dialog.setContentView(R.layout.dialog_pickup)
//    dialog.findViewById<BlurView>(R.id.blur_dialog).setupWith(rootView)
//        .setFrameClearDrawable(windowBackground)
//        .setBlurAlgorithm(RenderScriptBlur(activity))
//        .setBlurRadius(
//            5F
//        )
//    val dialogTitle = dialog.findViewById(R.id.text_title) as TextView
//    val dialogPositiveButton = dialog.findViewById(R.id.btn_yes) as TextView
//    val dialogNegativeButton = dialog.findViewById(R.id.btn_No) as TextView
//    dialogTitle.text = title
//    titleOfPositiveButton?.let { dialogPositiveButton.text = it } ?: dialogPositiveButton.makeGone()
//    titleOfNegativeButton?.let { dialogNegativeButton.text = it } ?: dialogNegativeButton.makeGone()
//    dialogPositiveButton.setOnClickListener {
//        positiveButtonFunction?.invoke()
//        dialog.dismiss()
//    }
//    dialogNegativeButton.setOnClickListener {
//        negativeButtonFunction?.invoke()
//        dialog.dismiss()
//    }
    dialog.show()
}


fun blur(context: Context?, image: Bitmap): Bitmap? {
    val width = image.width
    val height = image.height
    val inputBitmap = Bitmap.createScaledBitmap(
        image, width, height,
        false
    )
    val outputBitmap = Bitmap.createBitmap(inputBitmap)
    val rs: RenderScript = RenderScript.create(context)
    val theIntrinsic: ScriptIntrinsicBlur = ScriptIntrinsicBlur.create(
        rs,
        Element.U8_4(rs)
    )
    val tmpIn: Allocation = Allocation.createFromBitmap(rs, inputBitmap)
    val tmpOut: Allocation = Allocation.createFromBitmap(rs, outputBitmap)
    theIntrinsic.setRadius(24f)
    theIntrinsic.setInput(tmpIn)
    theIntrinsic.forEach(tmpOut)
    tmpOut.copyTo(outputBitmap)

    return outputBitmap
}

fun getPassenger(context: Context): PassengerSignup? {
    return getSavedObjectFromPreference(
        context,
        Constants.BAYRIDE_PASSENGER_MODEL,
        Constants.PASSENGER,
        PassengerSignup::class.java
    )
}

fun getDriver(context: Context): Driver? {
    return getSavedObjectFromPreference(
        context,
        Constants.BAYRIDE_DRIVER_MODEL,
        Constants.DRIVER,
        Driver::class.java
    )
}

fun getDriverProFile(context: Context): ProfileEntity? {
    return getSavedObjectFromPreference(
        context,
        Constants.BAYRIDE_DRIVER_MODEL,
        Constants.PROFILE,
        ProfileEntity::class.java
    )
}

fun getSignUpDetails(context: Context): SigUpResponse? {
    return getSavedObjectFromPreference(
        context,
        Constants.SIGN_UP_DETAILS,
        Constants.SIGN_UP,
        SigUpResponse::class.java
    )
}

fun getLoginDetails(context: Context): SignInResponse? {
    return getSavedObjectFromPreference(
        context,
        Constants.LOGIN_DETAILS,
        Constants.LOGIN,
        SignInResponse::class.java
    )
}

fun isValidEmail(target: String?): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(target.toString()).matches()
}

fun isValidPhoneNumber(target: CharSequence): Boolean {
    return if (target.length != 10) {
        false
    } else {
        Patterns.PHONE.matcher(target).matches()
    }
}

inline fun delay(delay: Long, crossinline completion: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        completion()
    }, delay)
}

//fun isAlphaNumeric(str: String?): Boolean {
//    val regex =
//    val p: Pattern = Pattern.compile(regex)
//
//    if (str == null) {
//        return false
//    }
//
//    val m: Matcher = p.matcher(str)
//    return m.matches()
//}

fun isAlphaNumeric(str: String): Pair<Boolean, String> {
    val number = Pattern.compile("([0-9])").matcher(str)
    val letter = Pattern.compile("([a-zA-Z])").matcher(str)

    if (str.length < 8) {
        return Pair(false, "Enter min 8 characters")
    } else if (str.length > 20) {
        return Pair(false, "Enter max 20 characters")
    } else if (!number.find()) {
        return Pair(false, "Password must contains one number")
    } else if (!letter.find()) {
        return Pair(false, "Password must contains one letter")
    }
    return Pair(true, "success")
}

fun makeCall(mobileNo: String, context: Context) {
    val number: Uri = Uri.parse("tel:$mobileNo")
    val callIntent = Intent(Intent.ACTION_DIAL, number)
    context.startActivity(callIntent)
}



