package com.example.fuelprices.utils

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.DateFormat
import java.util.*

class AxisValueFormatter : IndexAxisValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val emissionsMilliSince1970Time = value.toLong()
        val timeMilliseconds = Date(emissionsMilliSince1970Time)
        val dateTimeFormat: DateFormat =
            DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())

        return dateTimeFormat.format(timeMilliseconds)
    }
}