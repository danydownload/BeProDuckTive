package com.example.beproducktive.data.projects

import androidx.room.*
import com.example.beproducktive.data.projectandtasks.ProjectAndTasks
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM project_table")
    fun getProjects(): Flow<List<Project>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(project: Project)

    @Update
    suspend fun update(project: Project)

    @Delete
    suspend fun delete(project: Project)

/*
 * returns all instances of the data class that pairs the parent entity
 * and the child entity. This method requires Room to run two queries,
 * so add the @Transaction annotation to this method to ensure that
 * the whole operation is performed atomically.
 */
    @Transaction
    @Query("SELECT * FROM project_table")
    fun getProjectWithTasks() : Flow<List<ProjectAndTasks>>

    @Transaction
    @Query("SELECT * FROM project_table WHERE projectName = :projectName")
    fun getByProjectName(projectName: String?) : Flow<ProjectAndTasks>



}