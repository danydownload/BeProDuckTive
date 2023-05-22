package com.example.beproducktive.ui.addedittasks

import androidx.lifecycle.*
import com.example.beproducktive.data.projects.ProjectDao
import com.example.beproducktive.data.projects.ProjectRepository
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskDao
import com.example.beproducktive.data.tasks.TaskRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository
) : ViewModel() {

    val projects = projectRepository.getProjects().asLiveData()


    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName") ?: task?.taskTitle ?: ""
        set(value) {
            field = value
            state.set("taskTitle", value)
        }

    var taskDescription = state.get<String>("taskDescription") ?: task?.description ?: ""
        set(value) {
            field = value
            state.set("taskDescription", value)
        }

    var taskDeadlineFormatted = state.get<String>("taskDeadlineFormatted") ?: task?.deadlineFormatted ?: run {
        val currentDate = Calendar.getInstance().time
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        dateFormat.format(currentDate)
    }
        set(value) {
            field = value
            state.set("taskDeadlineFormatted", value)
        }

    var taskProject = state.get<String>("taskProject") ?: task?.belongsToProject ?: ""
        set(value) {
            field = value
            state.set("taskProject", value)
        }

    var taskPriority = state.get<Int>("taskPriority") ?: task?.priority ?: 0
        set(value) {
            field = value
            state.set("taskPriority", value)
        }

    var taskCompleted = state.get<Boolean>("taskCompleted") ?: task?.completed ?: false
        set(value) {
            field = value
            state.set("taskCompleted", value)
        }


    val projectNames: LiveData<List<String>> = projectRepository.getProjects().map { projects ->
        projects.map { it.projectName }
    }.asLiveData()


//    var taskArchived = state.get<Boolean>("taskArchived") ?: task?.archived ?: false
//        set(value) {
//            field = value
//            state.set("taskArchived", value)
//        }
//

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