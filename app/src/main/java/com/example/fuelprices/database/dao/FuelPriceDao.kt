package com.example.fuelprices.database.dao

import androidx.room.*
import com.example.fuelprices.database.model.FuelPrice
import kotlinx.coroutines.flow.Flow

@Dao
interface FuelPriceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFuelPrice(fuelPrice: FuelPrice)

    @Query("SELECT * FROM FUEL_PRICE ORDER BY date ASC")
    fun getPricesFlow(): Flow<List<FuelPrice>>

    @Delete
    suspend fun delete(fuelPrice: FuelPrice)
}