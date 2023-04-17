package com.example.beproducktive.ui.tasks

import android.util.Log
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.example.beproducktive.R
import com.example.beproducktive.data.projectandtasks.ProjectAndTasks
import com.example.beproducktive.data.projects.ProjectDao
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val projectDao: ProjectDao
) : ViewModel() {

//    val projects: LiveData<List<Project>> = liveData {
//        var project = projectDao.getProjects().first()
//        emit(project)
//    }
//

//

    val projects = projectDao.getProjects().asLiveData()


//    val tasks: LiveData<List<Task>> = liveData {
////        val firstProject = projectDao.getProjects().first()[0]
//        val firstProject = projectDao.getProjects().collect({
//            val nameProj = it[0].projectName
//        })
//
//
//        Log.e("Project", firstProject.projectName)
//        val proj = projectDao.getByProjectName(firstProject.projectName).first()[0]
//        emit(proj.tasks)
//    }

    val tasks: LiveData<List<Task>> = liveData {
        projectDao.getProjects().collect {
            if (it.isNotEmpty()) {

                val firstProject = it[0]
                Log.e("Project", firstProject.projectName)

                projectDao.getByProjectName(firstProject.projectName).collect {
                    emit(tasksOrderedByPriority(it))
                }
            }
        }
    }



    private fun emitTasksByProjectName(projectName: String) = liveData {
        projectDao.getByProjectName(projectName).collect {
            emit(tasksOrderedByPriority(it))
            // emit(tasksOrderedByDeadline(it))
            // emit(hideTasksCompleted(it)) //TODO: fix order to work also when app is started
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