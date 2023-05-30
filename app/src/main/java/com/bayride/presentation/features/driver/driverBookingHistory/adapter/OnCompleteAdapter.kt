package com.bayride.presentation.features.driver.driverBookingHistory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayride.data.models.dto.CompleteHistoryModel
import com.bayride.data.models.dto.OnGoingHistoryModel
import com.bayride.databinding.CompletedListItemBinding
import com.bayride.databinding.OngoingListItemBinding
import com.bumptech.glide.Glide

class OnCompleteAdapter : RecyclerView.Adapter<OnCompleteAdapter.ViewHolder>() {

    private var onCompleteList: ArrayList<CompleteHistoryModel> = arrayListOf()
    var onGoingItemClick: ((View) -> Unit)? = null

    fun setCompleteList(dataList: ArrayList<CompleteHistoryModel>) {
        this.onCompleteList = dataList
        notifyDataSetChanged()
    }

    class ViewHolder(var binding: CompletedListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val completeBinding = binding
        fun bind(completeHistoryModel: CompleteHistoryModel) {
            completeBinding.completeHistoryModel = completeHistoryModel
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            CompletedListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(holder.itemView.context).load(onCompleteList[position].driverImage)
            .into(holder.binding.driverProfileIcon)
        holder.bind(onCompleteList[position])

    }

    override fun getItemCount(): Int {
        return onCompleteList.size
    }
}
