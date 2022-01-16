package com.example.fuelprices.ui.fuelprice

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.fuelprices.R
import com.example.fuelprices.databinding.FragmentAddFuelPriceBinding
import com.example.fuelprices.ui.MainActivity
import com.example.fuelprices.utils.FuelTypeConstants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.*

@AndroidEntryPoint
class AddFuelPriceFragment : Fragment(R.layout.fragment_add_fuel_price) {
    private val viewModel: AddFuelPriceViewModel by viewModels()
    private lateinit var binding: FragmentAddFuelPriceBinding
    private var fuelTypes: List<String> = emptyList()
    private val args: AddFuelPriceFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddFuelPriceBinding.bind(view)

        (activity as MainActivity).supportActionBar?.title = resources.getText(R.string.edit)

        if (args.fuelPrice == null) {
            (activity as MainActivity).supportActionBar?.title = resources.getText(R.string.add)
        }

        setFuelTypeValue(args)
        fuelTypes = resources.getStringArray(R.array.fuel_types).toList()
        val adapter = ArrayAdapter(requireActivity(), R.layout.array_adapter_item, fuelTypes)
        binding.fuelTypeInput.setAdapter(adapter)

        binding.apply {
            dateInput.setText(formatDate(viewModel.fuelPriceDate))
            priceEditText.setText(viewModel.fuelPricePerLiter)

            fuelTypeInput.setOnItemClickListener { _, _, position, _ ->
                fuelTypeInput.setAdapter(adapter)
                viewModel.fuelPriceFuelType = position
            }

            priceEditText.addTextChangedListener {
                viewModel.fuelPricePerLiter = it.toString()
                viewModel.isPriceCorrect()
            }

            dateInput.addTextChangedListener {
                viewModel.fuelPriceDate = formatDateToLong(it.toString())
                viewModel.isDateInFuture()
            }

            dateInput.setOnClickListener {
                viewModel.dateAddButtonClick()
            }
        }

        binding.saveButton.setOnClickListener {
            lifecycleScope.launch {
                viewModel.onSaveClick()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect {
                when (it) {
                    is AddFuelPriceViewModel.Event.NavigateToAddDateScreen -> {
                        val action =
                            AddFuelPriceFragmentDirections.actionAddFuelPriceDialogToDatePickerDialog(
                                it.date
                            )
                        findNavController().navigate(action)
                    }
                    is AddFuelPriceViewModel.Event.SetPriceError -> {
                        binding.price.error = it.message
                        if (!it.correctValue) {
                            binding.price.isErrorEnabled = false
                        }
                    }
                    is AddFuelPriceViewModel.Event.SetDateError -> {
                        binding.date.error = it.message
                        if (!it.correctValue) {
                            binding.date.isErrorEnabled = false
                        }
                    }
                    is AddFuelPriceViewModel.Event.NavigateBack -> {
                        (activity as MainActivity).onBackPressed()
                    }
                    is AddFuelPriceViewModel.Event.ShowDoneMessage -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        observeDataFromDialogs()
    }

    private fun setFuelTypeValue(args: AddFuelPriceFragmentArgs) {
        binding.fuelTypeInput.setText(resources.getStringArray(R.array.fuel_types)[args.fuelPrice?.type
            ?: FuelTypeConstants.gasoline])
        viewModel.fuelPriceFuelType = args.fuelPrice?.type ?: FuelTypeConstants.gasoline
    }

    private fun observeDataFromDialogs() {
        val navController = findNavController()
        val navBackStackEntry = navController.getBackStackEntry(R.id.addFuelPriceFragment)
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME
                && navBackStackEntry.savedStateHandle.contains("date")
            ) {
                val result = navBackStackEntry.savedStateHandle.get<Long>("date")
                if (result != null) {
                    binding.dateInput.setText(formatDate(result))
                }
            }
        }
        navBackStackEntry.lifecycle.addObserver(observer)
        viewLifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                navBackStackEntry.lifecycle.removeObserver(observer)
            }
        })
    }

    private fun formatDateToLong(date: String): Long {
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        return dateFormat.parse(date).time
    }

    private fun formatDate(dateLong: Long): String {
        val date = Date(dateLong)
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        return dateFormat.format(date)
    }
}