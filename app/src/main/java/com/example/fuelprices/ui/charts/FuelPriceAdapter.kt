package com.example.fuelprices.ui.charts

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fuelprices.R
import com.example.fuelprices.database.model.FuelPrice
import com.example.fuelprices.databinding.RecyclerViewItemBinding
import java.text.DateFormat
import java.util.*


class FuelPriceAdapter(private val listener: OnItemClickListener) :
    ListAdapter<FuelPrice, FuelPriceAdapter.FuelPriceViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuelPriceViewHolder {
        val binding =
            RecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context))
        return FuelPriceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FuelPriceViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class FuelPriceViewHolder(private val binding: RecyclerViewItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                edit.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val maintenanceWithPlace = getItem(position)
                        listener.onItemClick(maintenanceWithPlace)
                    }
                }
                delete.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val maintenanceWithPlace = getItem(position)
                        listener.onItemDeleteClick(maintenanceWithPlace)
                    }
                }
            }
        }

        fun bind(fuelPrice: FuelPrice) {
            with(binding) {
                date.text = formatDate(fuelPrice.date)
                price.text = fuelPrice.pricePerLiter.toString()
                type.text = getFuelTypeValues(fuelPrice.type, root.context)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FuelPrice>() {
        override fun areItemsTheSame(
            oldItem: FuelPrice,
            newItem: FuelPrice,
        ): Boolean =
            oldItem.fuelPriceId == newItem.fuelPriceId

        override fun areContentsTheSame(
            oldItem: FuelPrice,
            newItem: FuelPrice,
        ): Boolean =
            oldItem == newItem
    }

    interface OnItemClickListener {
        fun onItemClick(fuelPrice: FuelPrice)
        fun onItemDeleteClick(fuelPrice: FuelPrice)
    }

    private fun formatDate(dateLong: Long): String {
        val date = Date(dateLong)
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        return dateFormat.format(date)
    }

    private fun getFuelTypeValues(fuelType: Int, context: Context): String {
        return context.resources.getStringArray(R.array.fuel_types)[fuelType]
    }
}