package com.example.beproducktive.ui.projects

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.beproducktive.data.projects.Project

class ProjectsAdapter(private val onClickListener: OnClickListener) : ListAdapter<Project, ProjectsViewHolder>(PROJECTS_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectsViewHolder =
        ProjectsViewHolder.create(parent)

    override fun onBindViewHolder(holder: ProjectsViewHolder, position: Int) {
        Log.d("ONCLICK", "onBindViewHolder PROJECTS")
        val currentItem = getItem(position)

        holder.itemView.setOnClickListener {
            Log.d("ONCLICK", "PROJECT SET ONCLICKED")
            onClickListener.onClick(currentItem)
        }

        holder.bind(currentItem)

    }

    companion object {
        private val PROJECTS_COMPARATOR = object : DiffUtil.ItemCallback<Project>() {
            override fun areItemsTheSame(oldItem: Project, newItem: Project): Boolean =
                oldItem.projectName == newItem.projectName

            override fun areContentsTheSame(oldItem: Project, newItem: Project): Boolean =
                oldItem == newItem

        }
    }

    class OnClickListener(val clickListener: (project: Project) -> Unit) {
        fun onClick(project: Project) = clickListener(project)
    }
}

