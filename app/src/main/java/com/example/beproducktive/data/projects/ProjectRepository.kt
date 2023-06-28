package com.example.beproducktive.data.projects

import com.example.beproducktive.data.projectandtasks.ProjectAndTasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val projectDao: ProjectDao
) {

    fun getProjects(): Flow<List<Project>> =
        projectDao.getProjects()

    fun getByProjectName(projectName: String?): Flow<ProjectAndTasks> =
        projectDao.getByProjectName(projectName)

    suspend fun insert(project: Project) {
        projectDao.insert(project)
    }

    suspend fun update(project: Project) {
        projectDao.update(project)
    }

    suspend fun delete(project: Project) {
        projectDao.delete(project)
    }

    fun getFirstProject(): Flow<Project> =
        projectDao.getFirstProject()

}