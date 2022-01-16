package com.example.fuelprices.database.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "fuel_price")
@Parcelize
data class FuelPrice(
    @PrimaryKey val fuelPriceId: Int?,
    val pricePerLiter: Double,
    val date: Long,
    val type: Int
) : Parcelable