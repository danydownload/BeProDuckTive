package com.example.beproducktive.ui.addedittasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.beproducktive.data.projects.ProjectDao
import com.example.beproducktive.data.projects.ProjectRepository
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskDao
import com.example.beproducktive.data.tasks.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    val projects = projectRepository.getProjects().asLiveData()

    // This function will be called when the user taps on the "Save" button
    fun editTask(task: Task) {
        viewModelScope.launch {
            taskRepository.update(task)
        }
    }

    fun addTask(newTask: Task) {
        viewModelScope.launch {
            taskRepository.insert(newTask)
        }
    }


}