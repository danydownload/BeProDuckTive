package com.example.beproducktive.ui.tasks

import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.beproducktive.R
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskPriority
import com.example.beproducktive.databinding.ItemTaskBinding


class TasksViewHolder(
    private val binding: ItemTaskBinding // autogenerated
) : RecyclerView.ViewHolder(binding.root) {

    fun  bind(task: Task) {
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

            val viewColor = when(task.priority)
            {
                TaskPriority.HIGH -> R.color.misty_rose
                TaskPriority.MEDIUM -> R.color.fawn
                TaskPriority.LOW -> R.color.baby_blue
            }

            viewLine.setBackgroundColor(ContextCompat.getColor(itemView.context, viewColor))

            cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, cardColor))
        }
    }

    companion object {
        fun create(parent: ViewGroup): TasksViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_task, parent, false)
            val binding = ItemTaskBinding.bind(view)
            return TasksViewHolder(binding)
        }
    }

}


