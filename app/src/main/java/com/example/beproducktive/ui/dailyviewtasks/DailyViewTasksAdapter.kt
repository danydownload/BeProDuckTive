package com.example.beproducktive.ui.dailyviewtasks

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.ListAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.beproducktive.R
import com.example.beproducktive.data.calendar.MyCalendar
import com.example.beproducktive.databinding.DateListRowBinding

class DailyViewTasksAdapter(
    private val onClickListener: OnClickListener
) : ListAdapter<MyCalendar, DailyViewTasksViewHolder>(CALENDAR_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewTasksViewHolder {
//        Log.d("RW-BIND", "DailyViewTasksAdapter.onCreateViewHolder() called")
        return DailyViewTasksViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: DailyViewTasksViewHolder, position: Int) {
//        Log.d("RW-BIND", "DailyViewTasksAdapter.onBindViewHolder() called")
        val currentItem = getItem(position)

        holder.itemView.setOnClickListener {
            onClickListener.onClick(currentItem)
        }

        holder.bind(currentItem)
    }

    companion object {
        private val CALENDAR_COMPARATOR = object : DiffUtil.ItemCallback<MyCalendar>() {
            override fun areItemsTheSame(oldItem: MyCalendar, newItem: MyCalendar): Boolean =
                oldItem.date == newItem.date

            override fun areContentsTheSame(oldItem: MyCalendar, newItem: MyCalendar): Boolean =
                oldItem == newItem

        }
    }

    class OnClickListener(val clickListener: (myCalendar: MyCalendar) -> Unit) {
        fun onClick(myCalendar: MyCalendar) = clickListener(myCalendar)
    }


}
