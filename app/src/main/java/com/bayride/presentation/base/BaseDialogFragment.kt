package com.bayride.presentation.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import com.bayride.databinding.FragmentBaseBinding
import com.bayride.common.views.SafetyClickListener

abstract class BaseDialogFragment : DialogFragment(), IBaseFragment {

    private lateinit var binding: FragmentBaseBinding

    override val safetyClick: SafetyClickListener by lazy { SafetyClickListener() }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            binding = FragmentBaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData(arguments)
        initViews()
        initActions()
        initObservers()
    }

    override fun onStart() {
        super.onStart()
        dialog?.apply {
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    override fun showLoading() {
        //binding.loadingViewComman.root.visibility = View.VISIBLE
    }

    override fun hideLoading() {
       // binding.loadingViewComman.root.visibility = View.INVISIBLE
    }

    @CallSuper
    override fun onDestroyView() {
        safetyClick.cleanListeners()
        super.onDestroyView()
    }
}