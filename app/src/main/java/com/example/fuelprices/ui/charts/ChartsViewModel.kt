package com.example.fuelprices.ui.charts

import androidx.lifecycle.*
import com.example.fuelprices.database.dao.FuelPriceDao
import com.example.fuelprices.database.model.FuelPrice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel @Inject constructor(
    private val fuelPriceDao: FuelPriceDao
) : ViewModel() {
    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    val getFuelPrices: LiveData<List<FuelPrice>> = fuelPriceDao.getPricesFlow().asLiveData()

    fun onUndoDeleteClick(fuelPrice: FuelPrice) = viewModelScope.launch {
        fuelPriceDao.insertFuelPrice(fuelPrice)
    }

    fun onItemClick(fuelPrice: FuelPrice) = viewModelScope.launch {
        eventChannel.send(
            Event.NavigateToEditScreen(fuelPrice)
        )
    }

    fun onDeleteClick(fuelPrice: FuelPrice) = viewModelScope.launch {
        fuelPriceDao.delete(fuelPrice)
        eventChannel.send(
            Event.ShowUndoDeleteMessage(fuelPrice)
        )
    }

    sealed class Event {
        data class NavigateToEditScreen(
            val fuelPrice: FuelPrice
        ) : Event()

        data class ShowUndoDeleteMessage(val fuelPrice: FuelPrice) : Event()
    }
}
