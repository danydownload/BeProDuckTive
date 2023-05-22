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
import com.example.beproducktive.ui.ADD_TASK_RESULT_OK
import com.example.beproducktive.ui.EDIT_TASK_RESULT_OK
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val state: SavedStateHandle,
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

    private val _tasksEventChannel = Channel<TasksEvent>()
    val tasksEvent = _tasksEventChannel.receiveAsFlow()


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

    fun onClickProject(findNavController: NavController) {
        findNavController.navigate(R.id.action_tasksFragment_to_projectsFragment)
    }

    fun onReceiveProject(projectName: String) =
        emitTasksByProjectName(projectName)


    fun onTaskSelected(projectName: String, task: Task) = viewModelScope.launch {
        _tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(projectName, task))
    }

    fun onClickAddTask() = viewModelScope.launch {
        _tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    fun onTimerSelected(task: Task) = viewModelScope.launch {
        _tasksEventChannel.send(TasksEvent.NavigateToTimerFragment(task))
    }

    fun onAddEditResult(result: Int) {
        Log.d("TasksViewModel", "onAddEditResult")
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }
    }

    // we want to send an event to the fragment because only fragments can show snackbar
    private fun showTaskSavedConfirmationMessage(text: String) = viewModelScope.launch {
        Log.d("TasksViewModel", "showTaskSavedConfirmationMessage")
        _tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(text))
    }

    fun onclickAddTask() = viewModelScope.launch {
        _tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
    }

    fun onDayClicked(dateSelected: String) = viewModelScope.launch {
        _tasksEventChannel.send(TasksEvent.RefreshTasks(dateSelected))
    }


    sealed class TasksEvent {

        object NavigateToAddTaskScreen : TasksEvent()
        data class NavigateToEditTaskScreen(val projectName: String, val task: Task) : TasksEvent()
        data class NavigateToTimerFragment(val task: Task) : TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : TasksEvent()

        data class RefreshTasks(val dateSelected: String) : TasksEvent()




    }

}