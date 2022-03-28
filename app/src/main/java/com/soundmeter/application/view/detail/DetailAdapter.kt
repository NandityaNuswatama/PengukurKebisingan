package com.soundmeter.application.view.detail

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.soundmeter.application.databinding.ItemDetailBinding
import com.soundmeter.application.domain.TimeStampDb

class DetailAdapter: ListAdapter<TimeStampDb, DetailAdapter.DetailViewHolder>(DetailComparator) {

    inner class DetailViewHolder(private val binding: ItemDetailBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: TimeStampDb){
            with(binding){
                
                tvTime.text = item.time
                tvDb.text = "${item.db} dB"
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = ItemDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DetailViewHolder(view)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object DetailComparator : DiffUtil.ItemCallback<TimeStampDb>() {
        override fun areItemsTheSame(oldItem: TimeStampDb, newItem: TimeStampDb): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: TimeStampDb, newItem: TimeStampDb): Boolean {
            return oldItem == newItem
        }

    }
}