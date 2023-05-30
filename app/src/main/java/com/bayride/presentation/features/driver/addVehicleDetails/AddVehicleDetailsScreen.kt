package com.bayride.presentation.features.driver.addVehicleDetails

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bayride.R
import com.bayride.common.utils.FileUtils
import com.bayride.common.views.*
import com.bayride.data.models.dto.VehicleDetails
import com.bayride.databinding.AddVahicleDetailsBinding
import com.bayride.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class AddVehicleDetailsScreen : BaseFragment<AddVahicleDetailsBinding>(),
    AdapterView.OnItemSelectedListener {
    private var colorAdapter: ArrayAdapter<String>? = null
    private var arrayAdapter: ArrayAdapter<String>? = null
    val viewModel: AddVehicleDetailsViewModel by viewModels()
    private var year: String? = null
    var color: String? = null

    var type: Int = 1

    companion object {
        const val VEHICLE_DETAILS = "VEHICLE_DETAILS"
    }

    override fun initData(data: Bundle?) {
        getLoginDetails(requireContext())?.info?.user_id?.let { viewModel.loadData(it) }
    }

    override fun initViews() {
        updateStatusBarColor(ContextCompat.getColor(requireActivity(), R.color.white))
        val yearList = arrayOf("2022", "2021", "2020", "2019", "2018", "2017")
        val colourList = arrayOf("Black", "White", "Blue", "Gray", "Yellow", "Pink")
        arrayAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, yearList)
        colorAdapter = ArrayAdapter(requireContext(), R.layout.spinner_item, colourList)
        binding.yearSpinner.adapter = arrayAdapter
        binding.colorSpinner.adapter = colorAdapter

        if (getDriverProFile(requireContext()) != null) {
            getDriverProFile(requireContext())?.info?.driver_info?.get(0)?.let {driverInfo->
                binding.editVehicleNumber.setText(driverInfo.vehicle_number)
                binding.edVehicleLicenseNumber.setText(driverInfo.vehicle_license_number)
                binding.edBrand.setText(driverInfo.vehicle_brand)
                binding.edModel.setText(driverInfo.vehicle_model)
                if (driverInfo.vehicle_year != null) {
                    arrayAdapter?.getPosition(driverInfo.vehicle_year)
                        ?.let { binding.yearSpinner.setSelection(it) }
                    colorAdapter?.getPosition(driverInfo.vehicle_colour)
                        ?.let { binding.colorSpinner.setSelection(it) }
                    driverInfo.vehicle_type_id?.let {
                        showSectionBackground(
                            it
                        )
                    }
                }
            }
        } else {

            binding.editVehicleNumber.setText(getVehicleDetails()?.vehicle_number)
            binding.edVehicleLicenseNumber.setText(getVehicleDetails()?.vehicle_license_number)
            binding.edBrand.setText(getVehicleDetails()?.brand)
            binding.edModel.setText(getVehicleDetails()?.model)
            if (getVehicleDetails()?.year != null) {
                arrayAdapter?.getPosition(getVehicleDetails()?.year)
                    ?.let { binding.yearSpinner.setSelection(it) }
                colorAdapter?.getPosition(getVehicleDetails()?.color)
                    ?.let { binding.colorSpinner.setSelection(it) }
                getVehicleDetails()?.type?.let { showSectionBackground(it) }
            }
        }

        binding.yearSpinner.onItemSelectedListener = this
        binding.colorSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                color = p0?.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //view?.hideKeyBoard()
            }
        }
        safetyClick.setViewClickSafetyListener(binding.btnBack) {
            findNavController().popBackStack()
        }
    }

    private fun showSectionBackground(type: Int) {
        when (type) {
            1 -> {
                setSectionBackground(binding.sedenLayout)
            }
            2 -> {
                setSectionBackground(binding.carNormalLayout)
            }
            3 -> {
                setSectionBackground(binding.vanLayout)
            }
            4 -> {
                setSectionBackground(binding.xuvLayout)
            }
        }
    }

    override fun initActions() {

        safetyClick.setViewClickSafetyListener(binding.sedenLayout) {
            setSectionBackground(binding.sedenLayout)
        }
        safetyClick.setViewClickSafetyListener(binding.carNormalLayout) {
            setSectionBackground(binding.carNormalLayout)
        }
        safetyClick.setViewClickSafetyListener(binding.vanLayout) {
            setSectionBackground(binding.vanLayout)
        }
        safetyClick.setViewClickSafetyListener(binding.xuvLayout) {
            setSectionBackground(binding.xuvLayout)
        }

        safetyClick.setViewClickSafetyListener(binding.btnSave) {
            if (binding.editVehicleNumber.text.toString()
                    .isEmpty() || binding.edBrand.text.toString()
                    .isEmpty() || binding.edModel.text.toString().isEmpty() ||
                binding.edVehicleLicenseNumber.text.toString().isEmpty()
            ) {
                context?.showAlertDialog(
                    title = getString(R.string.Error),
                    message = getString(R.string.enter_required_field),
                    getString(R.string.btn_ok),
                    true
                )
                return@setViewClickSafetyListener
            }
            viewModel.addVehicleDetails(
                binding.editVehicleNumber.text.toString(),
                binding.edVehicleLicenseNumber.text.toString(),
                year,
                binding.edModel.text.toString(),
                color,
                type,
                FileUtils.getRealPath(
                    requireContext(),
                    Uri.parse(getDriver(requireContext())?.vehicle_photo.toString())
                )?.let { it1 -> File(it1) },
                binding.edBrand.text.toString()
            )
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
                AddVehicleDetailSuccessFully -> {
                    if (viewModel.currentState.response?.Status == 1) {
                        findNavController().apply {
                            previousBackStackEntry?.savedStateHandle?.set(
                                VEHICLE_DETAILS,
                                Pair(
                                    VEHICLE_DETAILS, year?.let {
                                        color?.let { it1 ->
                                            VehicleDetails(
                                                binding.editVehicleNumber.text.toString(),
                                                binding.edVehicleLicenseNumber.text.toString(),
                                                binding.edBrand.text.toString(),
                                                binding.edModel.text.toString(),
                                                it,
                                                it1,
                                                type
                                            )
                                        }
                                    }
                                )
                            )
                            popBackStack()
                        }

                    } else {
                        context?.showAlertDialog(
                            title = getString(R.string.Error),
                            message = viewModel.currentState.response?.Message.toString(),
                            cancelable = true,
                            button = getString(R.string.btn_ok)
                        )
                    }
                }
                is AddVehicleDetailsErrorEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.toString() + " " + event.Message,
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
                is AddVehicleDetailsFailedEvent -> {
                    context?.showAlertDialog(
                        title = getString(R.string.Error),
                        message = event.error.message.toString(),
                        cancelable = true,
                        button = getString(R.string.btn_ok)
                    )
                }
            }
        }
    }

    private fun setSectionBackground(view: View) {
        if (view.id != binding.sedenLayout.id) {
            binding.carSedan.setTint(R.color.gray)
            binding.txtSedan.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray))
            binding.sedenLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.round_gray_rectengle)
        } else if (view.id == binding.sedenLayout.id) {
            type = 1
            binding.carSedan.setTint(R.color.vehicle_color)
            binding.txtSedan.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.vehicle_color
                )
            )
            binding.sedenLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.rounded_green_rectangle)
        }

        if (view.id != binding.carNormalLayout.id) {
            binding.carNormal.setTint(R.color.gray)
            binding.txtNormal.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray))
            binding.carNormalLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.round_gray_rectengle)
        } else if (view.id == binding.carNormalLayout.id) {
            type = 2

            binding.carNormal.setTint(R.color.vehicle_color)
            binding.txtNormal.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.vehicle_color
                )
            )
            binding.carNormalLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.rounded_green_rectangle)

        }
        if (view.id != binding.vanLayout.id) {
            binding.van.setTint(R.color.gray)
            binding.txtVan.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray))
            binding.vanLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.round_gray_rectengle)
        } else if (view.id == binding.vanLayout.id) {
            type = 3
            binding.van.setTint(R.color.vehicle_color)
            binding.txtVan.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.vehicle_color
                )
            )
            binding.vanLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.rounded_green_rectangle)

        }

        if (view.id != binding.xuvLayout.id) {
            binding.xuv.setTint(R.color.gray)
            binding.txtXuv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.gray))
            binding.xuvLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.round_gray_rectengle)
        } else if (view.id == binding.xuvLayout.id) {
            type = 4
            binding.xuv.setTint(R.color.vehicle_color)
            binding.txtXuv.setTextColor(
                ContextCompat.getColor(
                    requireActivity(),
                    R.color.vehicle_color
                )
            )
            binding.xuvLayout.background =
                ContextCompat.getDrawable(requireActivity(), R.drawable.rounded_green_rectangle)
        }


    }


    override fun onBackPressed() {
        super.onBackPressed()
        findNavController().popBackStack()
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        year = p0?.selectedItem.toString()

    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
    }

    private fun getVehicleDetails(): VehicleDetails? {
        return getDriver(requireContext())?.vehicleDetails
    }
}