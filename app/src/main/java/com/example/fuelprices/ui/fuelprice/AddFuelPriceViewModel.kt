package com.example.fuelprices.ui.fuelprice

import android.app.Application
import androidx.lifecycle.*
import com.example.fuelprices.R
import com.example.fuelprices.database.dao.FuelPriceDao
import com.example.fuelprices.database.model.FuelPrice
import com.example.fuelprices.utils.FuelTypeConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFuelPriceViewModel @Inject constructor(
    private val fuelPriceDao: FuelPriceDao,
    private val state: SavedStateHandle,
    private val application: Application
) : ViewModel() {
    private val fuelPrice = state.get<FuelPrice>("fuelPrice")

    private val eventChannel = Channel<Event>()
    val events = eventChannel.receiveAsFlow()

    private var fuelPriceId =
        state.get<Int>("fuelPriceId") ?: fuelPrice?.fuelPriceId
        set(value) {
            field = value
            state.set("fuelPriceId", value)
        }

    var fuelPricePerLiter: String =
        state.get<String>("fuelPricePerLiter")
            ?: if (fuelPrice?.pricePerLiter.toString() == "null") "" else fuelPrice?.pricePerLiter.toString()
                .replace(".", ",")
        set(value) {
            field = value
            state.set("fuelPricePerLiter", value)
        }

    var fuelPriceDate: Long =
        state.get<Long>("fuelPriceDate") ?: fuelPrice?.date ?: getDate()
        set(value) {
            field = value
            state.set("fuelPriceDate", value)
        }

    var fuelPriceFuelType =
        state.get<Int>("fuelPriceFuelType")
        set(value) {
            field = value
            state.set("fuelPriceFuelType", value)
        }

    suspend fun onSaveClick() {
        if (areInputsCorrect()) {
            fuelPriceDao.insertFuelPrice(
                FuelPrice(
                    fuelPriceId = if (fuelPriceId == null) null else fuelPriceId,
                    pricePerLiter = if (fuelPricePerLiter == "") 0.0 else fuelPricePerLiter.replace(
                        ",",
                        "."
                    ).toDouble(),
                    date = fuelPriceDate,
                    type = fuelPriceFuelType ?: FuelTypeConstants.gasoline
                )
            )
            showDoneMessage(application.resources.getString(R.string.done))
            navigateBack()
        }
    }

    private fun areInputsCorrect(): Boolean {
        return if (isPriceCorrect() && isDateInFuture()) {
            true
        } else {
            isPriceCorrect()
            isDateInFuture()
            false
        }
    }

    fun isPriceCorrect(): Boolean {
        return if (fuelPricePerLiter != "") {
            setPriceError(false, null)
            true
        } else {
            setPriceError(true, application.resources.getString(R.string.price_is_incorrect))
            false
        }
    }

    fun isDateInFuture(): Boolean {
        val current = System.currentTimeMillis()

        return if (fuelPriceDate <= current) {
            setDateError(
                false,
                null
            )
            true
        } else {
            setDateError(
                true,
                application.resources.getString(R.string.date_cannot_be_after_today)
            )
            false
        }
    }

    private fun getDate(): Long {
        return System.currentTimeMillis()
    }

    fun dateAddButtonClick() = viewModelScope.launch {
        eventChannel.send(
            Event.NavigateToAddDateScreen(
                fuelPriceDate
            )
        )
    }

    private fun setPriceError(correctValue: Boolean, message: String?) = viewModelScope.launch {
        eventChannel.send(
            Event.SetPriceError(correctValue, message)
        )
    }

    private fun setDateError(correctValue: Boolean, message: String?) = viewModelScope.launch {
        eventChannel.send(
            Event.SetDateError(correctValue, message)
        )
    }

    private fun navigateBack() = viewModelScope.launch {
        eventChannel.send(
            Event.NavigateBack
        )
    }

    private fun showDoneMessage(text: String) = viewModelScope.launch {
        eventChannel.send(
            Event.ShowDoneMessage(text)
        )
    }

    sealed class Event {
        data class NavigateToAddDateScreen(val date: Long) :
            Event()
        data class SetPriceError(val correctValue: Boolean, val message: String?) : Event()
        data class SetDateError(val correctValue: Boolean, val message: String?) : Event()
        object NavigateBack : Event()
        data class ShowDoneMessage(val message: String) : Event()
    }
}