package com.example.beproducktive.ui.dailyviewtasks

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beproducktive.R
import com.example.beproducktive.data.calendar.MyCalendar
import com.example.beproducktive.databinding.DateListRowBinding

class DailyViewTasksViewHolder(
    private val binding: DateListRowBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val tb_day: TextView = binding.day1
    private val tb_date: TextView = binding.date1

    fun bind(calendar: MyCalendar) {

//        Log.d("RW-BIND", "DailyViewTasksViewHolder.bind() called")

        binding.apply {

            // it takes all the data displayed by the recycler view, so at the end it will take the last element displayed
            tb_day.text = calendar.day
            tb_date.text = calendar.date

            if (calendar.isSelected) {

                // date1 and day1 contains the data of the element selected by the user
                println("date1: ${date1.text}")
                println("day1: ${day1.text}")

                date1.setTextColor(Color.CYAN)

                if (day1.text == "dom" || day1.text == "sun") {
                    day1.setTextColor(Color.RED)
                } else {
                    day1.setTextColor(Color.BLACK)
                }
            }
            else {
                date1.setTextColor(Color.BLACK)

                if (day1.text == "dom" || day1.text == "sun") {
                    day1.setTextColor(Color.RED)
                } else {
                    day1.setTextColor(Color.BLACK)
                }

                val currentDay = calendar.getCurrentDate().split("-")[0]
                if (currentDay == calendar.date) {
                    date1.setTextColor(Color.GREEN)
                }
            }

        }
    }


    companion object {
        fun create(parent: ViewGroup): DailyViewTasksViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.date_list_row, parent, false)
            val binding = DateListRowBinding.bind(view)
            return DailyViewTasksViewHolder(binding)
        }
    }

}