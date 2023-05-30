package com.bayride.presentation.features.emergencyContact.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayride.R
import com.bayride.data.datasources.remote.entities.EmergencyContactInfo
import com.bayride.data.models.dto.EmergencyContactModel
import com.bayride.databinding.ItemViewBinding
import com.bumptech.glide.Glide

class EmergencyContactAdapter : RecyclerView.Adapter<EmergencyContactAdapter.ViewHolder>() {
    var dataList: List<EmergencyContactInfo> = arrayListOf()

    class ViewHolder(var binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateList(dataList: List<EmergencyContactInfo>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemViewBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.binding.txtEmergencyContactName.text = dataList[position].contact_name
        Glide.with(holder.itemView.context).load(dataList[position].contact_profile_pic)
            .placeholder(R.drawable.ic_user_avatar)
            .into(holder.binding.imgEmergencyContact)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


}