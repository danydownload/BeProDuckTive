package com.example.beproducktive.ui.dailyviewtasks

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beproducktive.R
import com.example.beproducktive.data.calendar.MyCalendar
import com.example.beproducktive.databinding.DateListRowBinding


class CalendarAdapter(
    private val mCalendar: List<MyCalendar>,
    private val onClickListener: OnItemClickListener
) :
    RecyclerView.Adapter<CalendarAdapter.MyViewHolder>() {

    var recyclecount = 0
    var selectedPosition = -1


    interface OnItemClickListener {
        fun onItemClick(view: View, calendar: MyCalendar)
        fun onItemLongClick(view: View, calendar: MyCalendar)
    }


    inner class MyViewHolder(private val binding: DateListRowBinding) :
        RecyclerView.ViewHolder(binding.root), OnClickListener, View.OnLongClickListener {
        val tb_day: TextView = binding.day1
        val tb_date: TextView = binding.date1

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onClickListener.onItemClick(view, mCalendar[position])
            }
        }

        override fun onLongClick(view: View): Boolean {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                onClickListener.onItemLongClick(view, mCalendar[position])
            }
            return true
        }

    }

    // TODO - Create a ViewHolder class to represent each row item
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CalendarAdapter.MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.date_list_row, parent, false)

        val binding = DateListRowBinding.bind(view)

        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CalendarAdapter.MyViewHolder, position: Int) {
        val calendar: MyCalendar = mCalendar[position]

        holder.tb_day.text = calendar.day
        holder.tb_date.text = calendar.date

        if (calendar.isSelected) {
            holder.tb_date.setTextColor(Color.CYAN)

            if (holder.tb_day.text == "dom" || calendar.day == "sun") {
                holder.tb_day.setTextColor(Color.RED)
            } else {
                holder.tb_day.setTextColor(Color.BLACK)
            }

//            Log.d("Calendar", "[OBVH] ${calendar.date} is selected!")
        } else {
            holder.tb_date.setTextColor(Color.BLACK)

            if (holder.tb_day.text == "dom" || calendar.day == "sun") {
                holder.tb_day.setTextColor(Color.RED)
            } else {
                holder.tb_day.setTextColor(Color.BLACK)

                val day = calendar.getCurrentDate().split("-")[0]
                if (day == calendar.date) {
                    holder.tb_date.setTextColor(Color.GREEN)
                }
            }


        }







    }

    override fun getItemCount(): Int {
        return mCalendar.size
    }

    override fun onViewRecycled(holder: MyViewHolder) {
        recyclecount++
    }


}

