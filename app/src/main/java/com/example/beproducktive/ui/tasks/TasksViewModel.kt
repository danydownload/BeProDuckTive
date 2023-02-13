package com.example.beproducktive.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.navigation.NavController
import com.example.beproducktive.R
import com.example.beproducktive.data.projects.Project
import com.example.beproducktive.data.projects.ProjectDao
import com.example.beproducktive.data.tasks.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
//    private val taskDao: TaskDao,
    private val projectDao: ProjectDao
) : ViewModel() {

    val projects: LiveData<List<Project>> = liveData {
        val project = projectDao.getProjects().first()
        emit(project)
    }

    val tasks: LiveData<List<Task>> = liveData {
        val firstProject = projectDao.getProjects().first()[0]
        val proj = projectDao.getByProjectName(firstProject.projectName).first()[0]
        emit(proj.tasks)
    }


    private fun emitTasksByProjectName(projectName: String) = liveData {
        val proj = projectDao.getByProjectName(projectName).first()[0]
        emit(proj.tasks)
    }

    fun onclickProject(findNavController: NavController) {
        findNavController.navigate(R.id.action_tasksFragment_to_projectsFragment)
    }

    fun onReceiveProject(projectName: String) =
        emitTasksByProjectName(projectName)

}