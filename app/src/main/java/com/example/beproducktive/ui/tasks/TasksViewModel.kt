package com.example.beproducktive.ui.tasks

import android.util.Log
import androidx.lifecycle.*
import com.example.beproducktive.data.projectandtasks.ProjectAndTasks
import com.example.beproducktive.data.projects.Project
import com.example.beproducktive.data.projects.ProjectDao
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
//    private val taskDao: TaskDao,
    private val projectDao: ProjectDao
) : ViewModel() {


    val projects: LiveData<List<Project>> = liveData {
        val projects = projectDao.getProjects().first()
        emit(projects)
    }

    val tasks : LiveData<List<Task>> = liveData {
        val firstProject = projectDao.getProjects().first()[0].projectName
        val task = projectDao.getByProjectName(firstProject).first()[0]
        emit(task.tasks)
    }



}