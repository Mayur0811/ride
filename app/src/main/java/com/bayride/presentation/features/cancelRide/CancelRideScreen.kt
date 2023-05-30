package com.bayride.presentation.features.cancelRide

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.utils.Constants
import com.bayride.common.views.showAlertDialog
import com.bayride.databinding.AcceptCryptoScreenBinding
import com.bayride.databinding.CancelRideScreenBinding
import com.bayride.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CancelRideScreen : BaseFragment<CancelRideScreenBinding>() {

    val viewModel: CancelRideViewModel by viewModels()
    var reason: String? = null
    var listCheckBox: ArrayList<CheckBox> = arrayListOf()
    override fun initData(data: Bundle?) {

    }

    override fun initViews() {
        listCheckBox.add(binding.option)
        listCheckBox.add(binding.optionSecond)
        listCheckBox.add(binding.optionThird)
        listCheckBox.add(binding.optionFour)
        listCheckBox.add(binding.optionFive)
        listCheckBox.add(binding.optionSix)
        listCheckBox.add(binding.optionSeven)

        safetyClick.setViewClickSafetyListener(binding.btnCross) {
            Constants.ongoing = true
            findNavController().popBackStack()
        }

    }

    override fun initActions() {
        getCheckedReason()
        binding.btnCancelRide.setOnClickListener {
            if (reason != null) {
                Toast.makeText(requireActivity(), "$reason", Toast.LENGTH_SHORT).show()
            } else {
                setUncheck(binding.optionSeven, checked = false, flag = true)

            }
        }
        safetyClick.setViewClickSafetyListener(binding.btnCancelRide) {
            Constants.ongoing = true
            reason?.let { viewModel.cancelRide(it) }

        }
    }

    override fun initObservers() {
        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.loading }
        ) {
            if (it == true) showLoading() else hideLoading()
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                CancelRideSuccessEvent -> {
                    findNavController().popBackStack()
                }
                is CancelRideErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.Code.toString() + " " + event.Message,
                        button = getString(R.string.btn_ok)
                    )
                }
                is CancelRideFailEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.message.toString(),
                        button = getString(R.string.btn_ok)
                    )
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Constants.ongoing = true
        findNavController().popBackStack()

    }

    private fun getCheckedReason() {
        binding.option.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setUncheck(buttonView, isChecked)
            } else {
                binding.option.isChecked = false
            }
        }
        binding.optionSecond.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setUncheck(buttonView, isChecked)
            } else {
                binding.optionSecond.isChecked = false
            }
        }
        binding.optionThird.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setUncheck(buttonView, isChecked)
            } else {
                binding.optionThird.isChecked = false
            }

        }
        binding.optionFour.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setUncheck(buttonView, isChecked)
            } else {
                binding.optionFour.isChecked = false
            }
        }
        binding.optionFive.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setUncheck(buttonView, isChecked)
            } else {
                binding.optionFive.isChecked = false
            }
        }
        binding.optionSix.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setUncheck(buttonView, isChecked)
            } else {
                binding.optionSix.isChecked = false
            }

        }
        binding.optionSeven.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                setUncheck(buttonView, isChecked)
                binding.edComment.visibility = View.VISIBLE
            } else {
                binding.edComment.visibility = View.GONE
                binding.optionSeven.isChecked = false
            }

            binding.edComment.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    reason = s.toString()
                }
            })
        }
    }

    private fun setUncheck(button: View, checked: Boolean, flag: Boolean? = false) {
        if (flag == true) {
            if (button.id == binding.optionSeven.id) {
                Toast.makeText(
                    requireContext(),
                    "Please add reasons",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.please_select_any_reason),
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            listCheckBox.forEach {
                it.isChecked = if (it.id == button.id) {
                    checked
                } else {
                    false
                }
                if (it.isChecked) {
                    reason = if (binding.optionSeven.id == button.id) reason else it.text.toString()
                }
            }
        }

    }
}