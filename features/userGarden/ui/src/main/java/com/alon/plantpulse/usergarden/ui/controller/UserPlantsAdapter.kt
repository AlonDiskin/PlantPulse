package com.alon.plantpulse.usergarden.ui.controller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alon.plantpulse.plantsdetail.ui.databinding.UserPlantBinding
import com.alon.plantpulse.usergarden.ui.model.UserPlantUiState

class UserPlantsAdapter : PagingDataAdapter<UserPlantUiState, UserPlantsAdapter.PlantViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val binding = UserPlantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    class PlantViewHolder(private val binding: UserPlantBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserPlantUiState) {
            binding.plant = item
            binding.executePendingBindings()
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<UserPlantUiState>() {
            override fun areItemsTheSame(oldItem: UserPlantUiState, newItem: UserPlantUiState): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UserPlantUiState, newItem: UserPlantUiState): Boolean =
                oldItem == newItem
        }
    }
}
