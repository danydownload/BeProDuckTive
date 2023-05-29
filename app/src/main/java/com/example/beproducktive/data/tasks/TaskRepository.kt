package com.example.beproducktive.data.tasks

import android.util.Log
import com.example.beproducktive.data.SortOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {

    fun getTasks(
        projectName: String,
        query: String,
        sortOrder: SortOrder,
        hideCompleted: Boolean
    ): Flow<List<Task>> =
        taskDao.getTasks(projectName, query, sortOrder, hideCompleted)

    fun getTasksSortedByName(
        projectName: String,
        searchQuery: String,
        hideCompleted: Boolean
    ): Flow<List<Task>> =
        taskDao.getTasksSortedByName(projectName, searchQuery, hideCompleted)

    fun getTasksSortedByDeadline(
        projectName: String,
        searchQuery: String,
        hideCompleted: Boolean
    ): Flow<List<Task>> =
        taskDao.getTasksSortedByDeadline(projectName, searchQuery, hideCompleted)

    fun getTasksByDeadline(
        deadlineString: String,
        searchQuery: String,
        hideCompleted: Boolean
    ): Flow<List<Task>> =
        taskDao.getTasksByDeadline(deadlineString, searchQuery, hideCompleted)

    fun getTasksByProjectName(projectName: String): Flow<List<Task>> =
        taskDao.getTasksByProjectName(projectName)

    fun getTaskById(taskId: Int): Flow<Task?> =
        taskDao.getTaskById(taskId)

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    fun getProjectNameForTask(taskId: Int): Flow<String> =
        taskDao.getProjectNameForTask(taskId)

    fun deleteAllTasks() =
        taskDao.deleteAllTasks()

    fun deleteCompletedTasks() =
        taskDao.deleteCompletedTasks()

    fun getCompletedTasksForToday(currentDate: String): Flow<List<Task>> =
        taskDao.getCompletedTasksForToday(currentDate)

    fun getTasksForToday(currentDate: String): Flow<List<Task>> =
        taskDao.getTasksForToday(currentDate)

    fun getCompletedTasksBetweenDates(startDate: String, endDate: String): Flow<List<Task>> {
        Log.d("TaskRepository", "getCompletedTasksBetweenDates: $startDate, $endDate")
        return taskDao.getCompletedTasksBetweenDates(startDate, endDate)
    }


}