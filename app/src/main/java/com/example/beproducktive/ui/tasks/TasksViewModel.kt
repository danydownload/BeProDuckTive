package com.example.beproducktive.ui.tasks

import androidx.lifecycle.*
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

    val tasks : LiveData<List<Task>> = liveData {
        val firstProject = projectDao.getProjects().first()[0].projectName
        val task = projectDao.getByProjectName(firstProject).first()[0]
        emit(task.tasks)
    }

    private fun emitTasksByProjectName(projectName: String) {
        val tasks : LiveData<List<Task>> = liveData {
            val task = projectDao.getByProjectName(projectName).first()[0]
            emit(task.tasks)
        }
    }

    fun onclickProject(findNavController: NavController) {
        findNavController.navigate(R.id.action_tasksFragment_to_projectsFragment)
    }

    fun onReceiveProject(projectName: String) =
        emitTasksByProjectName(projectName)

}