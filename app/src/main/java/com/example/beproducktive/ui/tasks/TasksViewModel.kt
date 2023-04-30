package com.example.beproducktive.ui.tasks

import android.util.Log
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.example.beproducktive.R
import com.example.beproducktive.data.projectandtasks.ProjectAndTasks
import com.example.beproducktive.data.projects.ProjectDao
import com.example.beproducktive.data.projects.ProjectRepository
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskDao
import com.example.beproducktive.data.tasks.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {


    val projects = projectRepository.getProjects().asLiveData()


    val allTasks = taskRepository.getTasks().asLiveData()

    fun getTasksForDate(date: String?) = taskRepository.getTasksByDeadline(date!!).asLiveData()

    fun getProjectNameForTask(taskId: Int) = taskRepository.getProjectNameForTask(taskId).asLiveData()


    val tasks: LiveData<List<Task>> = liveData {
        projectRepository.getProjects().collect {
            if (it.isNotEmpty()) {

                val firstProject = it[0]
                Log.e("Project", firstProject.projectName)

                projectRepository.getByProjectName(firstProject.projectName).collect {
                    emit(tasksOrderedByPriority(it))
                }
            }
        }
    }


    private fun emitTasksByProjectName(projectName: String) = liveData {
        projectRepository.getByProjectName(projectName).collect {
            emit(tasksOrderedByPriority(it))
        }
    }

    fun tasksOrderedByPriority(projectAndTasks: ProjectAndTasks): List<Task> =
        projectAndTasks.tasks.sortedByDescending { it.priority }

    fun tasksOrderedByDeadline(projectAndTasks: ProjectAndTasks): List<Task> =
        projectAndTasks.tasks.sortedBy { it.deadline }

    fun hideTasksCompleted(projectAndTasks: ProjectAndTasks): List<Task> =
        projectAndTasks.tasks.filter { !it.completed }

    fun onclickProject(findNavController: NavController) {
        findNavController.navigate(R.id.action_tasksFragment_to_projectsFragment)
    }

    fun onReceiveProject(projectName: String) =
        emitTasksByProjectName(projectName)

}