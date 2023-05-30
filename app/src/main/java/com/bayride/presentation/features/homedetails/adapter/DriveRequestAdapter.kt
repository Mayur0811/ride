package com.bayride.presentation.features.homedetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bayride.R
import com.bayride.common.utils.Constants
import com.bayride.data.models.dto.DriverDetailsModel
import com.bayride.data.models.dto.DriverRequestInfo
import com.bayride.data.models.dto.Passenger
import com.bayride.databinding.DriveRequestItemBinding
import com.bumptech.glide.Glide

class DriveRequestAdapter : RecyclerView.Adapter<DriveRequestAdapter.ViewHolder>() {

    private var dataList: List<DriverRequestInfo> = arrayListOf()
    var onItemClick: ((View, DriverRequestInfo, String) -> Unit)? = null
    var onItemClickCard: ((DriverRequestInfo) -> Unit)? = null

    fun updateList(dataList: List<DriverRequestInfo>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: DriveRequestItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            DriveRequestItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(Constants.imageDomain + dataList[position].user_profile_pic)
            .into(holder.binding.imgDriver)
        holder.binding.txtDriverName.text = dataList[position].user_name
        holder.binding.txtDriverNumber.text = dataList[position].user_phone_number
        holder.binding.txtTotalRating.text = "(" + dataList[position].total_review + ")"
        holder.binding.txtCarBrand.text = dataList[position].vehicle_brand
        holder.binding.txtCarNumber.text = dataList[position].vehicle_number
        holder.binding.txtOfferRate.text = "$ " + dataList[position].driver_bid_price
        holder.binding.driverRating.rating = (dataList[position].real_star)?.toFloat() ?: 0.0F

        holder.itemView.setOnClickListener {
            onItemClickCard?.invoke(dataList[position])

        }
        holder.binding.llAccept.setOnClickListener {
            onItemClick?.invoke(it, dataList[position], "accept")
        }

        holder.binding.llReject.setOnClickListener {
            onItemClick?.invoke(it, dataList[position], "reject")
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


}