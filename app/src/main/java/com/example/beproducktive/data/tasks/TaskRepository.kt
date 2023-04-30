package com.example.beproducktive.data.tasks

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {

    fun getTasks() : Flow<List<Task>> =
        taskDao.getTasks()

    fun getTasksByProjectName(projectName: String) : Flow<List<Task>> =
        taskDao.getTasksByProjectName(projectName)

    fun getTaskById(taskId: Int) : Flow<Task?> =
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

    fun getTasksByDeadline(deadlineString: String) : Flow<List<Task>> =
        taskDao.getTasksByDeadline(deadlineString)

    fun getProjectNameForTask(taskId: Int) : Flow<String> =
        taskDao.getProjectNameForTask(taskId)

    fun deleteAllTasks() =
        taskDao.deleteAllTasks()

//    fun editTask(taskId: Int, title: String, description: String, deadline: String, priority: String, projectId: Int) {
//        taskDao.editTask(taskId, title, description, deadline, priority, projectId)
//    }
}