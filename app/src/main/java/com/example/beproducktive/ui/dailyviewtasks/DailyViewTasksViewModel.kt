package com.example.beproducktive.ui.dailyviewtasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DailyViewTasksViewModel @Inject constructor(
    private val taskDao: TaskDao
) : ViewModel() {

    val tasks = taskDao.getTasks().asLiveData()

    fun getTasksForDate(date: String?) = taskDao.getTasksByDeadline(date!!).asLiveData()


}


//    fun getTasksForDate(date: String?) = taskDao.getTasksForDate(date).asLiveData()