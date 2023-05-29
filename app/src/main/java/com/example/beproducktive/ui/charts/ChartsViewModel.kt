package com.example.beproducktive.ui.charts

import android.util.Log
import androidx.lifecycle.*
import com.example.beproducktive.data.calendar.MyCalendar
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskDao
import com.example.beproducktive.data.tasks.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChartsViewModel @Inject constructor(
     private val taskRepository: TaskRepository
) : ViewModel()
{

     val calendar = MyCalendar()
     val currentDate = calendar.getCurrentDate()

     val completedTasksForToday = taskRepository.getCompletedTasksForToday(currentDate).asLiveData()
     var completedTasksCountForToday = completedTasksForToday.value?.size ?: 0


     private val _totalTasksCountForToday: MutableLiveData<Int> = MutableLiveData()
     val totalTasksCountForToday: LiveData<Int> = _totalTasksCountForToday


     val allTasksForToday = taskRepository.getTasksForToday(currentDate).asLiveData()
     var allTasksCountForToday: Int = allTasksForToday.value?.size ?: 0


     private val startDate = calendar.getCurrentDateMinusSeven()

     val completedTasksBetweenDates: LiveData<List<Task>> =
          taskRepository.getCompletedTasksBetweenDates(startDate, currentDate).asLiveData()


}


