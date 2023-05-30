package com.bayride.presentation.features.driver.driverBookingHistory.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayride.R
import com.bayride.common.utils.Constants
import com.bayride.common.utils.toDate
import com.bayride.common.utils.toDateToLocal
import com.bayride.data.datasources.remote.entities.BookingHistoryInfo
import com.bayride.data.models.dto.OnGoingHistoryModel
import com.bayride.databinding.OngoingListItemBinding
import com.bumptech.glide.Glide

class OnGoingAdapter(var context: Context) : RecyclerView.Adapter<OnGoingAdapter.ViewHolder>() {

    var info: List<BookingHistoryInfo> = arrayListOf()
    var onGoingItemClick: ((View,BookingHistoryInfo) -> Unit)? = null

    fun setOnGoingList(info: List<BookingHistoryInfo>) {
        this.info = info
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: OngoingListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            OngoingListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = info[position]
        val profile = Constants.imageDomain + data.user_profile_pic
        holder.binding.apply {
            Glide.with(context).load(profile).placeholder(R.drawable.ic_user_avatar).into(driverProfileIcon)
            txtBookingId.text = "Booking Id:${data.fare_booking_unique_id}"
            txtFromLocation.text =
                "${data.from_address}, ${data.from_city}, ${data.from_country}"
            txtToLocation.text =
                "${data.to_address}, ${data.to_city}, ${data.to_country}"
            txtUserName.text = data.user_name
            txtUserPhoneNo.text = data.user_phone_number
            txtRideRate.text ="$ ${data.fare_cost_by_user}"
            dateAndTime.text = data.booked_at?.toDateToLocal()

            btnPickup.setOnClickListener {
                onGoingItemClick?.invoke(it,data)
            }
        }

    }

    override fun getItemCount(): Int {
        return info.size
    }
}