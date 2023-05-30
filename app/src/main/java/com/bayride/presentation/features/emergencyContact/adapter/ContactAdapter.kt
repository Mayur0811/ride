package com.bayride.presentation.features.emergencyContact.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayride.R
import com.bayride.data.models.dto.Contact
import com.bayride.data.models.dto.EmergencyContactModel
import com.bayride.databinding.ContectItemBinding
import com.bayride.databinding.ItemViewBinding
import com.bumptech.glide.Glide

class ContactAdapter : RecyclerView.Adapter<ContactAdapter.ViewHolder>() {
    var dataList: List<Contact> = arrayListOf()

    class ViewHolder(var binding: ContectItemBinding) : RecyclerView.ViewHolder(binding.root)

    var onContactClick: ((Contact) -> Unit)? = null

    fun ContactList(dataList: List<Contact>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ContectItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.binding.txtEmergencyContactName.text = dataList[position].name
        Glide.with(holder.itemView.context).load(dataList[position].photoURI).placeholder(R.drawable.ic_user_avatar)
            .into(holder.binding.imgEmergencyContact)
//        holder.binding.imgEmergencyContact.setImageURI(Uri.parse(dataList[position].photoURI))
        holder.itemView.setOnClickListener {
            onContactClick?.invoke(dataList[position])
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}