package com.example.beproducktive.ui.tasks


import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.beproducktive.R
import com.example.beproducktive.data.tasks.Task

class TasksAdapter(
    private val onClickListener: OnClickListener,
    private val onTimerClickListener: OnTimerClickListener,
    private val onCheckboxClickListener: OnCheckboxClickListener,
) : ListAdapter<Task, TasksViewHolder>(TASKS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder =
        TasksViewHolder.create(parent)

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val currentItem = getItem(position)

        holder.itemView.setOnClickListener {
            onClickListener.onClick(currentItem)
        }

        holder.itemView.findViewById<ImageView>(R.id.image_view_timer).setOnClickListener {
            onTimerClickListener.onTimerClick(currentItem)
        }

        val checkboxCompleted = holder.itemView.findViewById<CheckBox>(R.id.checkbox_completed)

        checkboxCompleted.setOnClickListener {
            onCheckboxClickListener.onCheckboxClick(currentItem, checkboxCompleted.isChecked)
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

    class OnTimerClickListener(val clickListener: (task: Task) -> Unit) {
        fun onTimerClick(task: Task) = clickListener(task)
    }

    class OnCheckboxClickListener(val clickListener: (task: Task, taskCompleted: Boolean) -> Unit) {
        fun onCheckboxClick(task: Task, taskCompleted: Boolean) = clickListener(task, taskCompleted)
    }
}

