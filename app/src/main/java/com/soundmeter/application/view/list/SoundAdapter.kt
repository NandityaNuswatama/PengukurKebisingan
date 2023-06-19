package com.soundmeter.application.view.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.soundmeter.application.R
import com.soundmeter.application.data.local.SoundEntity
import com.soundmeter.application.databinding.ItemSoundBinding

class SoundAdapter: ListAdapter<SoundEntity, SoundAdapter.SoundViewHolder>(SoundComparator) {

    var onItemClick: ((Int) -> Unit)?= null
    var onDeleteClick: ((Int) -> Unit)?= null

    inner class SoundViewHolder(private val binding: ItemSoundBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SoundEntity){
            with(binding){
                
                tvTitle.text = item.title
                tvDate.text = item.date
                tvDateUploaded.apply {
                    if (item.uploadedDate != "") visibility = View.VISIBLE
                    text = context.getString(R.string.format_uploaded_date, item.uploadedDate)
                }
                
                root.setOnClickListener {
                    onItemClick?.invoke(item.id)
                }
                
                btnDelete.setOnClickListener {
                    onDeleteClick?.invoke(item.id)
                }
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundViewHolder {
        val view = ItemSoundBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoundViewHolder(view)
    }

    override fun onBindViewHolder(holder: SoundViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object SoundComparator : DiffUtil.ItemCallback<SoundEntity>() {
        override fun areItemsTheSame(oldItem: SoundEntity, newItem: SoundEntity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: SoundEntity, newItem: SoundEntity): Boolean {
            return oldItem == newItem
        }
    }
}