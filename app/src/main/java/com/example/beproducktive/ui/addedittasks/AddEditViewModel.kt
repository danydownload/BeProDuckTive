package com.example.beproducktive.ui.addedittasks

import android.util.Log
import androidx.lifecycle.*
import com.example.beproducktive.data.projects.ProjectRepository
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskPriority
import com.example.beproducktive.data.tasks.TaskRepository
import com.example.beproducktive.ui.ADD_TASK_RESULT_OK
import com.example.beproducktive.ui.EDIT_TASK_RESULT_OK
import com.example.beproducktive.utils.Converters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
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

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = addEditTaskEventChannel.receiveAsFlow()


    val projects = projectRepository.getProjects().asLiveData()

    val task = state.get<Task>("task")

    val projectName = state.get<String>("projectName")

    var taskName = state.get<String>("taskName") ?: task?.taskTitle ?: ""
        set(value) {
            field = value
            state["taskTitle"] = value
        }

    var taskDescription = state.get<String>("taskDescription") ?: task?.description ?: ""
        set(value) {
            field = value
            state["taskDescription"] = value
        }

    var taskDeadlineFormatted: String =
        state.get<String>("taskDeadlineFormatted") ?: task?.deadlineFormatted ?: run {
            val currentDate = Calendar.getInstance().time
            val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            dateFormat.format(currentDate)
        }
        set(value) {
            field = value
            state["taskDeadlineFormatted"] = value
        }

    var taskProject = state.get<String>("taskProject") ?: task?.belongsToProject ?: ""
        set(value) {
            field = value
            state["taskProject"] = value
        }

    var taskPriority = state.get<Int>("taskPriority") ?: task?.priority ?: TaskPriority.LOW
        set(value) {
            field = value
            state["taskPriority"] = value
        }

    var taskCompleted = state.get<Boolean>("taskCompleted") ?: task?.completed ?: false
        set(value) {
            field = value
            state["taskCompleted"] = value
        }


    val projectNames: LiveData<List<String>> = projectRepository.getProjects().map { projects ->
        projects.map { it.projectName }
    }.asLiveData()


    fun onSaveClick() {
        if (taskName.isBlank()) {
            showInvalidInputMessage("Name cannot be empty")
            return
        }

        if (task != null) {
            val updatedTask = task.copy(
                taskTitle = taskName,
                description = taskDescription,
                priority = TaskPriority.valueOf(taskPriority.toString()),
                deadline = Converters.stringToDate(taskDeadlineFormatted),
                completed = taskCompleted,
                belongsToProject = taskProject
            )
            editTask(updatedTask)
        } else {
            Log.e("Priority", taskPriority.toString())
            val newTask = Task(
                taskTitle = taskName,
                description = taskDescription,
                priority = TaskPriority.valueOf(taskPriority.toString()),
                deadline = Converters.stringToDate(taskDeadlineFormatted),
                completed = taskCompleted,
                belongsToProject = taskProject
            )
            addTask(newTask)
        }
    }


    private fun editTask(task: Task) {
        viewModelScope.launch {
            taskRepository.update(task)
            addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
        }
    }

    private fun addTask(newTask: Task) {
        viewModelScope.launch {
            taskRepository.insert(newTask)
            addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
        }
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
    }

    sealed class AddEditTaskEvent {
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
        data class ShowInvalidInputMessage(val msg: String) : AddEditTaskEvent()
    }


}