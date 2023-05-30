package com.bayride.presentation.features.emergencyContact

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bayride.presentation.homeActivity.HomeActivity
import com.bayride.R
import com.bayride.common.views.showAlertDialog
import com.bayride.data.models.dto.VehicleDetails
import com.bayride.databinding.FragmentAddEmergencyContactBinding
import com.bayride.presentation.base.BaseFragment
import com.bayride.presentation.features.emergencyContact.adapter.EmergencyContactAdapter
import com.bayride.presentation.features.emergencyContact.adapter.SwipeHelper
import com.bayride.presentation.features.emergencyContact.contact.ContactScreen.Companion.CONTACT
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddEmergencyContactScreen : BaseFragment<FragmentAddEmergencyContactBinding>() {

    private val viewModel: AddEmergencyContactViewModel by viewModels()
    private val myAdapter = EmergencyContactAdapter()

    override fun initData(data: Bundle?) {
        viewModel.listEmergencyContact()
    }

    override fun initViews() {
        val spanText = SpannableString("You agree our Terms & Conditions")
        val clickSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"))
                startActivity(i)
            }
        }
        spanText.setSpan(clickSpan.apply {
            updateDrawState(TextPaint(Color.parseColor("#00000000")))
        }, 14, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spanText.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(requireActivity(), R.color.txtColor)),
            14,
            32,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.termsAndConditions.movementMethod = LinkMovementMethod.getInstance()
        binding.termsAndConditions.text = spanText
        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white))
        binding.rcvSwipe.adapter = myAdapter
        val swipeHelper = object : SwipeHelper(requireContext(), binding.rcvSwipe) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder?,
                underlayButtons: ArrayList<UnderlayButton>?
            ) {
                val scale = resources.displayMetrics.density
                val textSize = 12 * scale
                underlayButtons?.add(UnderlayButton(
                    "Delete",
                    textSize.toInt(),
                    0,
                    Color.RED,
                    object : UnderlayButtonClickListener {
                        override fun onClick(pos: Int) {
                            val list = viewModel.currentState.contactList
                            if (list != null) {
                                list[pos].emergency_contact_id?.let {
                                    viewModel.deleteEmergencyContact(
                                        it
                                    )
                                }
                            }
                        }
                    }
                ))
                Log.d("TAG", "instantiateUnderlayButton: ${underlayButtons?.size}")
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHelper)
        itemTouchHelper.attachToRecyclerView(binding.rcvSwipe)
    }


    override fun initActions() {
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            activity?.finishAffinity()
        }
        safetyClick.setViewClickSafetyListener(binding.termsAndConditionsLayout) {
            binding.termsAndConditionsCheckbox.isChecked =
                !binding.termsAndConditionsCheckbox.isChecked
        }
        safetyClick.setViewClickSafetyListener(binding.btnContinue) {
            if (binding.termsAndConditionsCheckbox.isChecked) {
                startActivity(Intent(requireContext(), HomeActivity::class.java))
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.accept_term_condition),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        safetyClick.setViewClickSafetyListener(binding.termsAndConditions) {
            binding.termsAndConditionsCheckbox.isChecked =
                !binding.termsAndConditionsCheckbox.isChecked
        }

        safetyClick.setViewClickSafetyListener(binding.btnAddEmergencyContact) {
            findNavController().navigate(R.id.contactScreen)
        }
    }

    override fun initObservers() {
        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.contactList }
        ) {
            if (it != null) {
                myAdapter.updateList(it)
            }
        }

        viewModel.store.observe(
            owner = viewLifecycleOwner,
            selector = { it.loading }
        ) {
            if (it == true) showLoading() else hideLoading()
        }

        viewModel.liveEvent.observe(this) { event ->
            when (event) {
                AddEmergencyContactSuccessEvent -> {

                }
                is AddEmergencyContactErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.code.toString() + " " + event.Message,
                        button = getString(R.string.btn_ok)
                    )
                }
                is AddEmergencyContactFailedEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.message.toString(),
                        button = getString(R.string.btn_ok)
                    )
                }
                DeleteEmergencyContactSuccessEvent -> {
                    viewModel.listEmergencyContact()
                }
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            getLiveData<Boolean>(CONTACT)
                .observe(viewLifecycleOwner) { result ->
                    if (result == true) {
                        viewModel.listEmergencyContact()
                    }
                    remove<VehicleDetails>(CONTACT)
                }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        activity?.finishAffinity()
    }
}