package com.example.beproducktive.ui.tasks


import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.beproducktive.R
import com.example.beproducktive.data.tasks.Task

class TasksAdapter() : ListAdapter<Task, TasksViewHolder>(TASKS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder =
        TasksViewHolder.create(parent)

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {

        val currentItem = getItem(position)
        if (currentItem != null)
            holder.bind(currentItem)
    }

    companion object {
        private val TASKS_COMPARATOR = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean =
                oldItem.taskId == newItem.taskId

            override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean =
                oldItem == newItem

        }
    }
}