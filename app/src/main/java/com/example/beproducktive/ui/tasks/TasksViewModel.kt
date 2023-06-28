package com.example.beproducktive.ui.tasks

import android.util.Log
import androidx.lifecycle.*
import androidx.navigation.NavController
import com.example.beproducktive.R
import com.example.beproducktive.data.PreferencesManager
import com.example.beproducktive.data.SortOrder
import com.example.beproducktive.data.projectandtasks.ProjectAndTasks
import com.example.beproducktive.data.projects.Project
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val taskRepository: TaskRepository,
    private val projectRepository: ProjectRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val projects = projectRepository.getProjects().asLiveData()

    val searchQuery = MutableStateFlow("")

    val preferencesFlow = preferencesManager.preferencesFlow

    val sortOrder = MutableStateFlow(SortOrder.BY_DEADLINE)
    val hideCompleted = MutableStateFlow(false)

    private val _deadline = MutableStateFlow("")
    val deadline: StateFlow<String> = _deadline

    fun setDeadline(deadline: String) {
        _deadline.value = deadline
    }

    fun isDeadlineNull(): Boolean {
        return _deadline.value.isEmpty()
    }


    private val tasksFlow = combine(
        searchQuery,
        preferencesFlow,
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskRepository.getTasks(
            projectName,
            query,
            filterPreferences.sortOrder,
            filterPreferences.hideCompleted
        )
    }

    val allTasks = tasksFlow.asLiveData()

    private val tasksByDeadlineFlow = combine(
        searchQuery,
        preferencesFlow,
        deadline
    ) { query, filterPreferences, deadlineValue ->
        Triple(query, filterPreferences, deadlineValue)
    }.flatMapLatest { (query, filterPreferences, deadlineValue) ->
        taskRepository.getTasksByDeadline(deadlineValue, query, filterPreferences.hideCompleted)
    }

    val allTasksByDeadline = tasksByDeadlineFlow.asLiveData()

    val firstProject = projectRepository.getFirstProject().asLiveData()

    var date = state.get<String>("date") ?: _deadline.value
        set(value) {
            field = value
            state["date"] = value
        }

    val project = state.get<Project>("project")

    var projectName = state.get<String>("projectName") ?: project?.projectName ?: ""
        set(value) {
            field = value
            state["projectName"] = value
        }


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

    fun getTasksForDate(date: String?) =
        taskRepository.getTasksByDeadline(date!!, "", true).asLiveData()

    fun getProjectNameForTask(taskId: Int) =
        taskRepository.getProjectNameForTask(taskId).asLiveData()

    private fun tasksOrderedByPriority(projectAndTasks: ProjectAndTasks): List<Task> =
        projectAndTasks.tasks.sortedByDescending { it.priority }

    fun tasksOrderedByDeadline(projectAndTasks: ProjectAndTasks): List<Task> =
        projectAndTasks.tasks.sortedBy { it.deadline }

    fun hideTasksCompleted(projectAndTasks: ProjectAndTasks): List<Task> =
        projectAndTasks.tasks.filter { !it.completed }


    fun onReceiveProject(projectName: String) =
        emitTasksByProjectName(projectName)


    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }


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

    fun onProjectClicked() = viewModelScope.launch {
        _tasksEventChannel.send(TasksEvent.NavigateToProjectScreen)
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskRepository.delete(task)
        _tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskRepository.insert(task)
    }

    fun onCheckboxSelected(task: Task, checked: Boolean) = viewModelScope.launch {
        taskRepository.update(task.copy(completed = checked))
    }

    fun onDeleteAllCompletedClick() = viewModelScope.launch {
        _tasksEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedScreen)
    }


    sealed class TasksEvent {

        object NavigateToAddTaskScreen : TasksEvent()
        object NavigateToProjectScreen : TasksEvent()

        object NavigateToDeleteAllCompletedScreen : TasksEvent()
        data class NavigateToEditTaskScreen(val projectName: String, val task: Task) : TasksEvent()
        data class NavigateToTimerFragment(val task: Task) : TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val msg: String) : TasksEvent()
        data class RefreshTasks(val dateSelected: String) : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TasksEvent()


    }

}
