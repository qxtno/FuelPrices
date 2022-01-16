package com.example.fuelprices.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fuelprices.database.dao.FuelPriceDao
import com.example.fuelprices.database.model.FuelPrice

@Database(
    entities = [FuelPrice::class],
    version = 1
)

abstract class Database : RoomDatabase() {
    abstract fun refuelingDao(): FuelPriceDao
}