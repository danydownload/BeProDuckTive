package com.example.beproducktive.ui.dailyviewtasks

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.beproducktive.R
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskPriority
import com.example.beproducktive.databinding.FragmentDailyViewTasksBinding
import com.example.beproducktive.databinding.ItemTaskBinding

class DailyViewTasksViewHolder(
    private val binding: ItemTaskBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(task: Task) {
        binding.apply {
            checkboxCompleted.isChecked = task.completed
            textviewTaskTitle.text = task.taskTitle
            setTaskPriority(task, cardView)
            deadline.text = task.deadlineFormatted
            textViewDescription.text = task.description
        }
    }

    private fun setTaskPriority(task: Task, cardView: CardView) {
        binding.apply {
            priority.text = itemView.context.resources.getString(
                R.string.priority_value,
                task.priority.name
            )
            priority.setTypeface(null, Typeface.BOLD)

            val colorArray = itemView.context.resources.getIntArray(R.array.colors)

            val textColor = when (task.priority) {
                TaskPriority.HIGH -> R.color.red
                TaskPriority.MEDIUM -> R.color.green
                TaskPriority.LOW -> R.color.resolution_blue
            }
            val intColor = ContextCompat.getColor(itemView.context, textColor)
            priority.setTextColor(intColor)
            deadline.setTextColor(intColor)

            val cardColor = when(task.priority)
            {
                TaskPriority.HIGH -> R.color.pale_pink
                TaskPriority.MEDIUM -> R.color.light_goldenrod_yellow
                TaskPriority.LOW -> R.color.water
            }

            val drawable = cardView.background
            val wrappedDrawable = DrawableCompat.wrap(drawable)
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(itemView.context, cardColor))
        }
    }

    companion object {
        fun create(parent: ViewGroup): DailyViewTasksViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task, parent, false)
            val binding = ItemTaskBinding.bind(view)
            return DailyViewTasksViewHolder(binding)
        }
    }

}

