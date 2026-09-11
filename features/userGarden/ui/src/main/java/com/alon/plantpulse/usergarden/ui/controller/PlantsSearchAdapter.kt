package com.alon.plantpulse.usergarden.ui.controller

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alon.plantpulse.plantsdetail.ui.databinding.PlantItemBinding
import com.alon.plantpulse.usergarden.ui.model.PlantUiState

class PlantsSearchAdapter(
    private val addPlantClickListener: (PlantUiState) -> Unit
) : PagingDataAdapter<PlantUiState, PlantsSearchAdapter.PlantViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val binding = PlantItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    inner class PlantViewHolder(private val binding: PlantItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.addPlantButton.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    if (item != null) {
                        addPlantClickListener(item)
                    }
                }
            }
        }

        fun bind(item: PlantUiState) {
            binding.plant = item
            binding.executePendingBindings()
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<PlantUiState>() {
            override fun areItemsTheSame(oldItem: PlantUiState, newItem: PlantUiState): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PlantUiState, newItem: PlantUiState): Boolean =
                oldItem == newItem
        }
    }
}
