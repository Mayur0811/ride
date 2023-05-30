package com.bayride.presentation.features.countrycodedialog.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bayride.common.views.getFlag
import com.bayride.data.models.dto.CountryModel
import com.bayride.databinding.CountrycodeItemLayoutBinding
import com.bumptech.glide.Glide

class CountryCodeAdapter : RecyclerView.Adapter<CountryCodeAdapter.ViewHolder>() {
    var dataList: ArrayList<Pair<String, String>>? = null
    var context: Context? = null

    class ViewHolder(var binding: CountrycodeItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    var onCountryCode: ((Pair<String, String>) -> Unit)? = null

    fun countryList(dataList: ArrayList<Pair<String, String>>, context: Context) {
        this.dataList = dataList
        this.context = context
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = CountrycodeItemLayoutBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.binding.txtEmergencyContactName.text = ("+" + dataList?.get(position)?.second)
      //  holder.binding.flagEmoji.text = dataList?.get(position)?.first?.let { getFlag(it) }
        context?.let { context ->
            Glide.with(context).load(dataList?.get(position)?.first?.let { getFlag(it) })
                .into(holder.binding.flagEmoji)
        }
        holder.itemView.setOnClickListener {
            dataList?.let { it1 -> onCountryCode?.invoke(it1[position]) }
        }
    }

    override fun getItemCount(): Int {
        return dataList?.size ?: 0
        //  return dataList?.code?.size ?: 0
    }


}