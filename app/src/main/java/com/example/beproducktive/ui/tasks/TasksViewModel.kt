package com.example.beproducktive.ui.tasks

import android.util.Log
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.example.beproducktive.R
import com.example.beproducktive.data.projectandtasks.ProjectAndTasks
import com.example.beproducktive.data.projects.Project
import com.example.beproducktive.data.projects.ProjectDao
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskDao
import com.example.beproducktive.data.tasks.TaskPriority
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
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
        }
    }


    fun tasksOrderedByPriority(it: ProjectAndTasks) : List<Task> {
        val tasksPriorityHigh: MutableList<Task> = mutableListOf();
        val tasksPriorityMedium: MutableList<Task> = mutableListOf();
        val tasksPriorityLow: MutableList<Task> = mutableListOf();

        for (singleTask in it.tasks) {
            if (singleTask.priority == TaskPriority.HIGH)
                tasksPriorityHigh.add(singleTask)
            else if (singleTask.priority == TaskPriority.MEDIUM)
                tasksPriorityMedium.add(singleTask)
            else
                tasksPriorityLow.add(singleTask)
        }

        val tasksOrderedByPriority: MutableList<Task> = mutableListOf();
        tasksOrderedByPriority.addAll(tasksPriorityHigh)
        tasksOrderedByPriority.addAll(tasksPriorityMedium)
        tasksOrderedByPriority.addAll(tasksPriorityLow)

        val tasksList = buildList<Task> {
            addAll(tasksOrderedByPriority)
        }

        return tasksList
    }




    fun onclickProject(findNavController: NavController) {
        findNavController.navigate(R.id.action_tasksFragment_to_projectsFragment)
    }

    fun onReceiveProject(projectName: String) =
        emitTasksByProjectName(projectName)

}