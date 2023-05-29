package com.example.beproducktive.ui.charts

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.beproducktive.R
import com.example.beproducktive.data.calendar.MyCalendar
import com.example.beproducktive.databinding.FragmentChartsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ChartsFragment : Fragment(R.layout.fragment_charts) {

    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart
    private lateinit var binding: FragmentChartsBinding

    private val viewModel: ChartsViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentChartsBinding.bind(view)

        barChart = binding.barChart
        pieChart = binding.pieChart


        setupPieChart()

        setupBarChart()

        observeCompletedTasks()

        observeCompletedTasksCountByDay()

    }


    private fun observeCompletedTasksCountByDay() {
        viewModel.completedTasksBetweenDates.observe(viewLifecycleOwner) { tasks ->

            Log.d("ChartsFragment", "observeCompletedTasksCountByDay: $tasks")

            val completedTasksCountByDay: MutableMap<String, Int> = mutableMapOf()

            tasks.forEach { task ->
                val deadline =
                    task.deadlineFormatted // Assuming deadline is in the format "dd-mm-yyyy"

                val dayNumber = deadline.substring(0, 2) // Extract the day number from the deadline
                val count = completedTasksCountByDay[dayNumber] ?: 0
                completedTasksCountByDay[dayNumber] = count + 1
            }

            // Use completedTasksCountByDay map to display the counts in the bar chart
            updateBarChart(completedTasksCountByDay)
        }

    }

    private fun setupBarChart() {
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.description.isEnabled = false
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(false)
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)
        barChart.xAxis.setDrawGridLines(false)
        barChart.legend.isEnabled = true
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.setDrawAxisLine(true)
        barChart.xAxis.granularity = 1f


    }


    private fun updateBarChart(completedTasksCountByDay: MutableMap<String, Int>) {
        val barChart: BarChart = binding.barChart

        val entries: ArrayList<BarEntry> = ArrayList()

        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val labels: ArrayList<String> = ArrayList() // List to store labels for X-axis

        val dateFormat = SimpleDateFormat("MM/yyyy", Locale.getDefault())


        // Iterate through the last 7 days starting from today
        for (i in 6 downTo 0) {
            val dayNumber = currentDay - i

            // Get the count for the current day, or 0 if it doesn't exist in the map
            val count = completedTasksCountByDay[dayNumber.toString()] ?: 0
            val barEntry = BarEntry(dayNumber.toFloat(), count.toFloat())
            entries.add(barEntry)

            val dayMonthYear = "${dayNumber}/${dateFormat.format(calendar.time)}"
            labels.add(dayMonthYear) // Add dayNumber/month/year as label for X-axis
        }

        binding.barChartTitleTV2.text = "From ${labels[0]} to ${labels[6]}"


        val barDataSet = BarDataSet(entries, "Tasks Completed")
        barDataSet.colors = ColorTemplate.MATERIAL_COLORS.asList()

        val xAxis: XAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels) // Set custom label formatter
        xAxis.setCenterAxisLabels(true)

        val barData = BarData(barDataSet)

        barChart.data = barData

        barChart.invalidate()
    }


    private fun setupPieChart() {
        pieChart.setUsePercentValues(true)
        pieChart.description = Description().apply {
            text = "% Tasks solved"
        }
        pieChart.isDrawHoleEnabled = true
        pieChart.setHoleColor(android.R.color.transparent)
        pieChart.setTransparentCircleColor(android.R.color.transparent)
        pieChart.setTransparentCircleAlpha(0)
        pieChart.holeRadius = 50f
        pieChart.transparentCircleRadius = 55f
        pieChart.isRotationEnabled = false
    }


    private fun observeCompletedTasks() {

        val calendar = MyCalendar()
        val currentDate = calendar.getCurrentDate()

        viewModel.completedTasksForToday.observe(viewLifecycleOwner) { tasks ->
            Log.d("ChartsFragment", "COMPLETED TASK OF TODAY: ${tasks.size}")
            viewModel.completedTasksCountForToday = tasks.size
        }

        viewModel.allTasksForToday.observe(viewLifecycleOwner) { tasks ->
            Log.d("ChartsFragment", "TOTAL TASK OF TODAY: ${tasks.size}")
            viewModel.allTasksCountForToday = tasks.size

            if (viewModel.allTasksCountForToday != 0)
                updatePieChart(
                    viewModel.completedTasksCountForToday,
                    viewModel.allTasksCountForToday
                )
            else
                showNoTasksMessage()
        }
    }

    private fun showNoTasksMessage() {
        val pieChart: PieChart = binding.pieChart
        pieChart.clear()
        pieChart.setNoDataText("No tasks scheduled for today")
        pieChart.invalidate()
    }

    private fun updatePieChart(completedTasksCount: Int, totalTasksCount: Int) {
        val pieChart: PieChart = binding.pieChart

        val entries: ArrayList<PieEntry> = ArrayList()

        entries.add(PieEntry(completedTasksCount.toFloat(), "Completed: $completedTasksCount"))
        entries.add(
            PieEntry(
                (totalTasksCount - completedTasksCount).toFloat(),
                "Remaining: ${totalTasksCount - completedTasksCount}"
            )
        )


        val pieDataSet = PieDataSet(entries, "Tasks")
        pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.asList()
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.invalidate()


    }


}


