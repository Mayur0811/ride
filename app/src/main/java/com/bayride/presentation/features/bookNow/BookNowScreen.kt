package com.bayride.presentation.features.bookNow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.databinding.FragmentBookNowScreenBinding
import com.bayride.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_book_now_screen.*

@AndroidEntryPoint
class BookNowScreen : BaseFragment<FragmentBookNowScreenBinding>() {

    private val radioButtonList: ArrayList<RadioButton> = arrayListOf()

    override fun initData(data: Bundle?) {

    }

    override fun initViews() {
        radioButtonList.add(binding.radioCreditCard)
        radioButtonList.add(binding.radioBitcoin)
        radioButtonList.add(binding.radioETH)
        radioButtonList.add(binding.radioDodge)
        radioButtonList.add(binding.radioRideNode)
        binding.radioCreditCard.isChecked = true
    }

    override fun initActions() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnBookNow.setOnClickListener {
//            findNavController().navigate(R.id.creditCardPaymentScreen)
        }
        binding.optionCreditCard.setOnClickListener {
            uncheckOthers(binding.radioCreditCard)
        }
        binding.optionBitcoin.setOnClickListener {
//            uncheckOthers(binding.radioBitcoin)
        }
        binding.optionETH.setOnClickListener {
//            uncheckOthers(binding.radioETH)
        }
        binding.optionDodge.setOnClickListener {
//            uncheckOthers(binding.radioDodge)
        }
        binding.optionRideNode.setOnClickListener {
//            uncheckOthers(binding.radioRideNode)
        }
    }

    override fun initObservers() {

    }

    private fun uncheckOthers(radioButton: RadioButton) {
        radioButton.isChecked = true
        radioButtonList.forEach {
            if (it.isChecked && radioButton.id != it.id) {
                it.isChecked = false
                return@forEach
            }
        }
    }
}