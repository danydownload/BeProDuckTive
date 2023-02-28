package com.example.beproducktive.ui.projects

import android.util.Log
import androidx.lifecycle.*
import com.example.beproducktive.data.projects.Project
import com.example.beproducktive.data.projects.ProjectDao
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskDao
import com.example.beproducktive.data.tasks.TaskPriority
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectDao: ProjectDao
) : ViewModel() {


//    val projects: LiveData<List<Project>> = liveData {
//        val project = projectDao.getProjects().first()
//
//        for (p in project) {
//            Log.d("PROJC-UP", p.projectName)
//        }
//
//        emit(project)
//    }

    val projects = projectDao.getProjects().asLiveData()


}