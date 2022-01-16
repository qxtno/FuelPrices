package com.example.fuelprices.ui.charts

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fuelprices.R
import com.example.fuelprices.database.model.FuelPrice
import com.example.fuelprices.databinding.FragmentChartsBinding
import com.example.fuelprices.utils.AxisValueFormatter
import com.example.fuelprices.utils.FuelTypeConstants
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class ChartsFragment : Fragment(R.layout.fragment_charts), FuelPriceAdapter.OnItemClickListener {
    private lateinit var binding: FragmentChartsBinding
    private val viewModel: ChartsViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChartsBinding.bind(view)

        val fuelPriceAdapter = FuelPriceAdapter(this)
        binding.recyclerView.apply {
            adapter = fuelPriceAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        viewModel.getFuelPrices.observe(viewLifecycleOwner) {
            fuelPriceAdapter.submitList(it.reversed())
            val gasolineEntries = ArrayList<Entry>()
            val dieselEntries = ArrayList<Entry>()
            val lpgEntries = ArrayList<Entry>()

            it.forEach { list ->
                when (list.type) {
                    FuelTypeConstants.gasoline -> {
                        gasolineEntries.add(
                            Entry(
                                list.date.toFloat(),
                                list.pricePerLiter.toFloat()
                            )
                        )
                    }
                    FuelTypeConstants.diesel -> {
                        dieselEntries.add(
                            Entry(
                                list.date.toFloat(),
                                list.pricePerLiter.toFloat()
                            )
                        )
                    }
                    FuelTypeConstants.lpg -> {
                        lpgEntries.add(
                            Entry(
                                list.date.toFloat(),
                                list.pricePerLiter.toFloat()
                            )
                        )
                    }
                }
            }

            val gasolineDataSet = LineDataSet(
                gasolineEntries,
                resources.getStringArray(R.array.fuel_types)[0]
            )

            val dieselDataSet = LineDataSet(
                dieselEntries,
                resources.getStringArray(R.array.fuel_types)[1]
            )

            val lpgDataSet = LineDataSet(
                lpgEntries,
                resources.getStringArray(R.array.fuel_types)[2]
            )
            gasolineDataSet.color = ContextCompat.getColor(requireContext(), R.color.green)
            gasolineDataSet.setCircleColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.font_color
                )
            )
            gasolineDataSet.valueTextColor =
                ContextCompat.getColor(requireContext(), R.color.font_color)

            dieselDataSet.color = ContextCompat.getColor(requireContext(), R.color.font_color)
            dieselDataSet.setCircleColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.font_color
                )
            )
            dieselDataSet.valueTextColor =
                ContextCompat.getColor(requireContext(), R.color.font_color)

            lpgDataSet.color = ContextCompat.getColor(requireContext(), R.color.light_blue)
            lpgDataSet.setCircleColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.font_color
                )
            )
            lpgDataSet.valueTextColor =
                ContextCompat.getColor(requireContext(), R.color.font_color)

            binding.chart.data = LineData(gasolineDataSet, dieselDataSet, lpgDataSet)
            binding.chart.invalidate()
        }

        binding.chart.apply {
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.valueFormatter = AxisValueFormatter()
            description.isEnabled = false
            fitScreen()
            axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.font_color)
            axisRight.textColor = ContextCompat.getColor(requireContext(), R.color.font_color)
            xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.font_color)
            legend.textColor = ContextCompat.getColor(requireContext(), R.color.font_color)
            description.textColor = ContextCompat.getColor(requireContext(), R.color.font_color)
        }

        binding.addFab.setOnClickListener {
            findNavController().navigate(R.id.addFuelPriceFragment)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.events.collect { event ->
                when (event) {
                    is ChartsViewModel.Event.NavigateToEditScreen -> {
                        val action =
                            ChartsFragmentDirections.actionDieselStatsFragmentToAddFuelPriceFragment(
                                event.fuelPrice
                            )
                        findNavController().navigate(action)
                    }
                    is ChartsViewModel.Event.ShowUndoDeleteMessage -> {
                        val snackbar = Snackbar.make(
                            requireView(), resources.getString(R.string.deleted),
                            Snackbar.LENGTH_LONG
                        )
                        snackbar.anchorView = binding.addFab
                        snackbar.setAction(resources.getString(R.string.restore)) {
                            viewModel.onUndoDeleteClick(event.fuelPrice)
                        }.show()
                    }
                }
            }
        }
    }

    override fun onItemClick(fuelPrice: FuelPrice) {
        viewModel.onItemClick(fuelPrice)
    }

    override fun onItemDeleteClick(fuelPrice: FuelPrice) {
        viewModel.onDeleteClick(fuelPrice)
    }
}