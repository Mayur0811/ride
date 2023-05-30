package com.bayride.presentation.base

import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.widget.ProgressBar
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.activity.addCallback
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.bayride.common.views.SafetyClickListener
import com.bayride.databinding.FragmentBaseBinding
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VB:ViewBinding> : Fragment(), IBaseFragment {

    override val baseActivity: BaseActivity?
        get() = activity as? BaseActivity

    private lateinit var baseBinding: FragmentBaseBinding

    lateinit var binding: VB

    private var progress: ProgressBar? = null
    override val safetyClick: SafetyClickListener by lazy { SafetyClickListener() }

    fun updateStatusBarColor(color: Int, isDark: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.window?.let { window ->
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = color
                invertInsets(isDark, window)
            }
        }
    }


    @Suppress("DEPRECATION")
    fun invertInsets(darkTheme: Boolean, window: Window) {
        if (Build.VERSION.SDK_INT >= 30) {
            val statusBar = APPEARANCE_LIGHT_STATUS_BARS
            val navBar = APPEARANCE_LIGHT_NAVIGATION_BARS
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


    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        baseBinding = FragmentBaseBinding.inflate(inflater, container, false)
        val type = javaClass.genericSuperclass
        val clazz = (type as ParameterizedType).actualTypeArguments[0] as Class<VB>
        val method = clazz.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        binding=(method.invoke(null, layoutInflater, container, false) as VB)
        baseBinding.contentContainer.addView(binding.root)
//        inflater.inflate(
//            getLayoutId(), binding.contentContainer, true)
        return baseBinding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData(arguments)
        initViews()
        initActions()
        initObservers()
        (requireActivity() as OnBackPressedDispatcherOwner).onBackPressedDispatcher.addCallback(
            viewLifecycleOwner
        ) {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
//        if (findNavController().previousBackStackEntry == null) {
////            val mainFragment = parentFragment?.parentFragment?.parentFragment as? LoginScreen
////            mainFragment?.onBackPressed() ?:
//            requireActivity().finish()
//        } else {
//            findNavController().popBackStack()
//        }
    }

    override fun showLoading() {
        progress?.visibility = View.VISIBLE
        baseBinding.loadingViewComman.root.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        progress?.visibility = View.GONE
        baseBinding.loadingViewComman.root.visibility = View.GONE

    }

    @CallSuper
    override fun onDestroyView() {
        safetyClick.cleanListeners()
        super.onDestroyView()
    }

}