package com.example.beproducktive.ui.timer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TimerSharedViewModel @Inject constructor(
    private val taskRepository: TaskRepository) : ViewModel() {

    private val _timeLeft = MutableLiveData<String>()
    val timeLeft: LiveData<String> = _timeLeft

    private val _isTimerRunning = MutableLiveData<Boolean>()
    val isTimerRunning: LiveData<Boolean> = _isTimerRunning

    private val _isTimerStarted = MutableLiveData<Boolean>()
    val isTimerStarted: LiveData<Boolean> = _isTimerStarted

    private val _isPauseStarted = MutableLiveData<Boolean>()
    val isPauseStarted: LiveData<Boolean> = _isPauseStarted

    private val _destination = MutableLiveData<String>()
    val destinationLiveData: LiveData<String> = _destination

    private val _task = MutableLiveData<Task>()
    val task: LiveData<Task> = _task

    fun updateTask(task: Task) {
        _task.value = task
    }

    fun updateDestination(destination: String) {
        _destination.value = destination
    }


    fun updateIsPauseStarted(isStarted: Boolean) {
        _isPauseStarted.value = isStarted
    }


    fun updateTimeLeft(time: String) {
//        Log.d("Timer_cd ", "ViewModel: TimeLeft UPDATED: $time")
        _timeLeft.value = time
    }

    fun updateIsTimerRunning(isRunning: Boolean) {
//        Log.d("Timer_cd ", "ViewModel: IsTimerRunning UPDATED: $isRunning")
        _isTimerRunning.value = isRunning
    }

    fun updateIsTimerStarted(isStarted: Boolean) {
//        Log.d("Timer_cd ", "ViewModel: IsTimerStarted UPDATED: $isStarted")
        _isTimerStarted.value = isStarted
    }

    fun updateTaskCompleted(task: Task) = viewModelScope.launch {
        val taskLatest = taskRepository.getTaskById(task.taskId)
        val newTask = taskLatest.first()?.copy(completed = true)
        taskRepository.update(newTask!!)
    }




}