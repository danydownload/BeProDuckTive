package com.example.beproducktive.data.tasks

import androidx.room.*
import com.example.beproducktive.data.projectandtasks.ProjectAndTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM task_table")
    fun getTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE belongsToProject = :belongsToProject")
    fun getTasksByProjectName(belongsToProject: String) : Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM task_table WHERE deadline = :deadlineString ORDER BY CASE priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 3 END ASC")
    fun getTasksByDeadline(deadlineString: String): Flow<List<Task>>


}