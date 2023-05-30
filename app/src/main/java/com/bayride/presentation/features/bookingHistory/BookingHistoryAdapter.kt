package com.bayride.presentation.features.bookingHistory

import android.annotation.SuppressLint
import android.content.Context
import android.view.*
import android.widget.RatingBar
import androidx.recyclerview.widget.RecyclerView
import com.bayride.R
import com.bayride.common.utils.Constants
import com.bayride.common.utils.toDateToLocal
import com.bayride.data.datasources.remote.entities.BookingHistoryInfo
import com.bayride.databinding.BookingHistoryItemBinding
import com.bumptech.glide.Glide
import me.zhanghai.android.materialratingbar.MaterialRatingBar
import java.util.*

class BookingHistoryAdapter(private val c: Context) :
    RecyclerView.Adapter<BookingHistoryAdapter.ViewHolder>() {

    var info: List<BookingHistoryInfo> = arrayListOf()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = info[position]
        val driverProfile = Constants.imageDomain + data.user_profile_pic
        holder.binding.apply {
            Glide.with(c).load(driverProfile).placeholder(R.drawable.ic_user_avatar)
                .into(driverProfileIcon)
            txtFromLocation.text =
                "${data.from_address}, ${data.from_city}, ${data.from_country}"
            txtToLocation.text =
                "${data.to_address}, ${data.to_city}, ${data.to_country}"
            driverName.text = data.user_name
            txtRete.text = "$${data.fare_booking_amount}"
            if (data.real_star != null) {
                ratingBar.rating = data.real_star.toFloat()
            }
            dateAndTime.text = data.created_at?.toDateToLocal()
        }
    }

    override fun getItemCount(): Int {
        return info.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            BookingHistoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    fun updateData(info: List<BookingHistoryInfo>) {
        this.info = info
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: BookingHistoryItemBinding) : RecyclerView.ViewHolder(binding.root)
}