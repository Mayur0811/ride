package com.bayride.presentation.features.signup

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayride.R
import com.bayride.common.sharedpreference.getSavedObjectFromPreference
import com.bayride.common.utils.Constants
import com.bayride.common.views.getPassenger
import com.bayride.common.views.visible
import com.bayride.data.models.dto.Driver
import com.bayride.data.models.dto.Passenger
import com.bayride.databinding.ItemOptionLayoutBinding

class SignUpDetailsAdapter(val context: Context) :
    RecyclerView.Adapter<SignUpDetailsAdapter.ViewHolder>() {

    private var signupDetailsList: ArrayList<Passenger>? = arrayListOf()
    private var driverDetails: ArrayList<String>? = arrayListOf()
    private var countPassenger: ArrayList<Int> = arrayListOf()
    private var countDriver: ArrayList<Int> = arrayListOf()

    var onItemClick: ((Passenger) -> Unit)? = null
    var onItemClickDriver: ((String) -> Unit)? = null

    fun setDetailsFillList(
        details: ArrayList<Passenger>? = null,
        driverDetails: ArrayList<String>? = null
    ) {
        this.signupDetailsList = details
        this.driverDetails = driverDetails
        countPassenger.clear()
        countDriver.clear()
        notifyDataSetChanged()
    }


    class ViewHolder(var binding: ItemOptionLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemOptionLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (signupDetailsList != null) {
            holder.binding.number.text = (position + 1).toString()
            holder.binding.nameTitle.text = signupDetailsList?.get(position)?.title

            getPassenger(context)?.apply {
                when (signupDetailsList?.get(position)?.title) {
                    context.resources.getString(R.string.passenger_upload_photo) -> {
                        if (photo != null) {
                            setData(holder, position)
                            countPassenger.add(position)
                        }
                    }
                    context.resources.getString(R.string.passenger_Name) -> {
                        if (name != null) {
                            setData(holder, position)
                            countPassenger.add(position)

                        }
                    }
                    Constants.CREATE_USERNAME -> {
                        if (username != null) {
                            setData(holder, position)
                            countPassenger.add(position)
                        }
                    }
                    Constants.EMAIL -> {
                        if (email != null) {
                            setData(holder, position)
                            countPassenger.add(position)
                        }
                    }
                    Constants.PHONE_NUMBER -> {
                        if (phoneNumber != null) {
                            setData(holder, position)
                            countPassenger.add(position)
                        }
                    }
                    Constants.ADDRESS -> {
                        if (address != null) {
                            setData(holder, position)
                            countPassenger.add(position)
                        }
                    }
                    Constants.CREATE_PASSWORD -> {
                        if (createPassword != null) {
                            setData(holder, position)
                            countPassenger.add(position)
                        }
                    }
                    Constants.SIGNATURE -> {
                        if (signature != null) {
                            setData(holder, position)
                            countPassenger.add(position)
                        }
                    }
                    Constants.ENTER_PAYMENT_DETAILS -> {
                        if (enterPaymentDetails != null) {
                            setData(holder, position)
                            countPassenger.add(position)
                        }
                    }
                }

            }
            if (position == countPassenger.size) {
                holder.binding.apply {
                    icLockGreen.visible(true)
                    nameTitle.background =
                        context.resources.getDrawable(
                            R.drawable.round_green_rectangle_with_cut,
                            null
                        )
                    number.setTextColor(context.resources.getColor(R.color.txtColor, null))
                    nameTitle.setTextColor(context.resources.getColor(R.color.txtColor, null))
                    linearlayout.background =
                        context.resources.getDrawable(R.drawable.circle_border, null)
                }
            }
            if (position < countPassenger.size + 1) {
                holder.binding.textView.setOnClickListener {
                    signupDetailsList?.get(position)?.let { it1 -> onItemClick?.invoke(it1) }
                }

            }
        } else {
            holder.binding.number.text = (position + 1).toString()
            holder.binding.nameTitle.text = driverDetails?.get(position)

            getSavedObjectFromPreference(
                context,
                Constants.BAYRIDE_DRIVER_MODEL,
                Constants.DRIVER,
                Driver::class.java
            )?.apply {
                when (driverDetails?.get(position)) {
                    Constants.CONTACT_INFORMATION -> {
                        if (contactInformation != null) {
                            setData(holder, position)
                            countDriver.add(position)
                        }
                    }
                    Constants.VEHICLE_PHOTO -> {
                        if (vehicle_photo != null) {
                            setData(holder, position)
                            countDriver.add(position)

                        }
                    }
                    Constants.VEHICLE_DETAILS -> {
                        if (vehicleDetails != null) {
                            setData(holder, position)
                            countDriver.add(position)
                        }
                    }
                    Constants.UPLOAD_DRIVING_LICENCES -> {
                        if (uploadDriverLicense != null) {
                            setData(holder, position)
                            countDriver.add(position)
                        }
                    }
                    Constants.INSURANCE_INFORMATION -> {
                        if (insuranceInformation != null) {
                            setData(holder, position)
                            countDriver.add(position)
                        }
                    }
                    Constants.UPLOAD_INSURANCE_CARD -> {
                        if (uploadInsuranceCard != null) {
                            setData(holder, position)
                            countDriver.add(position)
                        }
                    }
                    Constants.ACCEPTS_CRYPTO -> {
                        if (accepts_crypto != null) {
                            setData(holder, position)
                            countDriver.add(position)
                        }
                    }
                    Constants.ACCEPTS_PETS -> {
                        if (accepts_pets != null) {
                            setData(holder, position)
                            countDriver.add(position)
                        }
                    }
                    Constants.BANK_DETAILS -> {
                        if (bankDetails != null) {
                            setData(holder, position)
                            countDriver.add(position)
                        }
                    }
                }

            }

            if (position == countDriver.size) {
                holder.binding.apply {
                    icLockGreen.visible(true)
                    nameTitle.background =
                        context.resources.getDrawable(
                            R.drawable.round_green_rectangle_with_cut,
                            null
                        )
                    number.setTextColor(context.resources.getColor(R.color.txtColor, null))
                    nameTitle.setTextColor(context.resources.getColor(R.color.txtColor, null))
                    linearlayout.background =
                        context.resources.getDrawable(R.drawable.circle_border, null)
                }
            }
            if (position < countDriver.size + 1) {
                holder.binding.textView.setOnClickListener {
                    driverDetails?.get(position)?.let { it1 -> onItemClickDriver?.invoke(it1) }
                }
            }
        }


    }

    private fun setData(holder: ViewHolder, pos: Int) {
        holder.binding.apply {
            nameTitle.background =
                context.resources.getDrawable(
                    R.drawable.round_green_rectangle_with_cut,
                    null
                )
            number.setTextColor(context.resources.getColor(R.color.txtColor, null))
            nameTitle.setTextColor(context.resources.getColor(R.color.txtColor, null))
            linearlayout.background =
                context.resources.getDrawable(R.drawable.circle_border, null)
            icChecked.visible(true)
            icLock.visible(false)

        }

    }

    override fun getItemCount(): Int {
        return if (signupDetailsList != null)
            signupDetailsList?.size!!
        else
            driverDetails?.size!!

    }

}
