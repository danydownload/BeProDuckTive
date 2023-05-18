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
import java.text.SimpleDateFormat
import java.util.*

class DailyViewTasksViewHolder(
    private val binding: DateListRowBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val tb_month: TextView = binding.month1
    private val tb_day: TextView = binding.day1
    private val tb_date: TextView = binding.date1

    fun bind(calendar: MyCalendar) {

//        Log.d("RW-BIND", "DailyViewTasksViewHolder.bind() called")

        binding.apply {

            // it takes all the data displayed by the recycler view, so at the end it will take the last element displayed
            tb_day.text = calendar.day
            tb_date.text = calendar.date

            val monthName = calendar.getMonthFromInt(calendar.month!!.toInt())
            tb_month.text = monthName
            tb_month.setTextColor(Color.BLACK)


            if (calendar.isSelected) {

                date1.setTextColor(Color.CYAN)

                if (day1.text == "dom" || day1.text == "Sun") {
                    day1.setTextColor(Color.RED)
                } else {
                    day1.setTextColor(Color.BLACK)
                }
            } else {
                date1.setTextColor(Color.BLACK)

                if (day1.text == "dom" || day1.text == "Sun") {
                    day1.setTextColor(Color.RED)
                } else {
                    day1.setTextColor(Color.BLACK)
                }

                val currentDay = calendar.getCurrentDate().split("-")[0].toInt()
                if (currentDay == calendar.date!!.toInt()) {
                    date1.setTextColor(Color.GREEN)
                }
//                Log.e("RW-BIND", "currentDay: $currentDay, calendar.date: ${calendar.date}")

            }

        }
    }


    companion object {
        fun create(parent: ViewGroup): DailyViewTasksViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.date_list_row, parent, false)
            val binding = DateListRowBinding.bind(view)
            return DailyViewTasksViewHolder(binding)
        }
    }

}