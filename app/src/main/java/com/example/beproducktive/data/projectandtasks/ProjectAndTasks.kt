package com.example.beproducktive.data.projectandtasks

import androidx.room.Embedded
import androidx.room.Relation
import com.example.beproducktive.data.projects.Project
import com.example.beproducktive.data.tasks.Task
import kotlinx.coroutines.flow.Flow

data class ProjectAndTasks(
    @Embedded val project: Project,
    @Relation(
        parentColumn = "projectName",
        entityColumn = "belongsToProject"
    )
    val tasks: List<Task>
)
