package com.example.beproducktive.ui.projects

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.beproducktive.data.projects.Project
import com.example.beproducktive.data.projects.ProjectDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectDao: ProjectDao
) : ViewModel() {

    val projects: LiveData<List<Project>> = liveData {
        val project = projectDao.getProjects().first()

        for (p in project) {
            Log.d("PROJC-UP", p.projectName)
        }

        emit(project)
    }

//    val projects = projectDao.getProjects().asLiveData()


}