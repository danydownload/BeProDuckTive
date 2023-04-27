package com.example.beproducktive.ui.tasks


import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.beproducktive.data.tasks.Task

class TasksAdapter(
    private val onClickListener: OnClickListener
) : ListAdapter<Task, TasksViewHolder>(TASKS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder =
        TasksViewHolder.create(parent)

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val currentItem = getItem(position)

        holder.itemView.setOnClickListener {
            onClickListener.onClick(currentItem)
        }

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

    class OnClickListener(val clickListener: (task: Task) -> Unit) {
        fun onClick(task: Task) = clickListener(task)
    }
}