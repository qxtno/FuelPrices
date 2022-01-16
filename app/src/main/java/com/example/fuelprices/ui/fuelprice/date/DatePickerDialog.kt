package com.example.fuelprices.ui.fuelprice.date

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import java.util.*

class DatePickerDialog : DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar: Calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = formatDatePickerDateToString(year, month, dayOfMonth)
        findNavController().previousBackStackEntry?.savedStateHandle?.set(
            "date",
            date
        )
    }

    private fun formatDatePickerDateToString(year: Int, month: Int, day: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return calendar.timeInMillis
    }
}