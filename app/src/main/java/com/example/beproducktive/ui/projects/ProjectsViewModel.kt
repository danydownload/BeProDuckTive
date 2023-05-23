package com.example.beproducktive.ui.projects

import android.util.Log
import androidx.lifecycle.*
import com.example.beproducktive.data.projects.Project
import com.example.beproducktive.data.projects.ProjectDao
import com.example.beproducktive.data.projects.ProjectRepository
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskDao
import com.example.beproducktive.data.tasks.TaskPriority
import com.example.beproducktive.ui.addedittasks.AddEditViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProjectsViewModel @Inject constructor(
    private val projectRepository: ProjectRepository
) : ViewModel() {

    var projName: String = ""
    val projects = projectRepository.getProjects().asLiveData()

    private val addProjectEventChannel = Channel<ProjectsEvent>()
    val addProjectEvent = addProjectEventChannel.receiveAsFlow()


    fun onClickNewProject(projectName: String) {
        if (projectName.isBlank()) {
            showInvalidInputMessage("Project name cannot be empty.")
            return
        }
        addProject(projectName)
    }

    private fun addProject(projectName: String) = viewModelScope.launch {
        val project = Project(projectName)
        projectRepository.insert(project)
        addProjectEventChannel.send(ProjectsEvent.AddProject(project))
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addProjectEventChannel.send(ProjectsEvent.ShowInvalidInputMessage(text))
    }

    fun onProjectSwiped(project: Project) = viewModelScope.launch {
        projectRepository.delete(project)
        addProjectEventChannel.send(ProjectsEvent.ShowUndoDeleteProjectMessage(project))
    }

    fun onUndoDeleteClick(project: Project) = viewModelScope.launch {
        projectRepository.insert(project)
    }


    sealed class ProjectsEvent {
        data class AddProject(val project: Project) : ProjectsEvent()
        data class ShowInvalidInputMessage(val msg: String) : ProjectsEvent()

        data class ShowUndoDeleteProjectMessage(val project: Project) : ProjectsEvent()
    }

}