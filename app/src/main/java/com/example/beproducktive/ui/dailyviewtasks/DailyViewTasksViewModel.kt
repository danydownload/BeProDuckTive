package com.example.beproducktive.ui.dailyviewtasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.beproducktive.data.tasks.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DailyViewTasksViewModel @Inject constructor (
    private val taskDao: TaskDao
    ) : ViewModel() {

//    fun getTasksForDate(date: String?) {
//        val tasks = taskDao.getTasksForDate(date).asLiveData()
//
//    }


    val tasks = taskDao.getTasks().asLiveData()

    }