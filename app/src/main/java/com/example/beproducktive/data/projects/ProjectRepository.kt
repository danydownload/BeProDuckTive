package com.example.beproducktive.data.projects

import com.example.beproducktive.data.projectandtasks.ProjectAndTasks
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val projectDao: ProjectDao
) {

        fun getProjects() : Flow<List<Project>> =
            projectDao.getProjects()

        fun getProjectWithTasks() : Flow<List<ProjectAndTasks>> =
            projectDao.getProjectWithTasks()

        fun getByProjectName(projectName: String?) : Flow<ProjectAndTasks> =
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

        fun getProjectNameForTask(taskId: Int) =
            projectDao.getProjectNameForTask(taskId)

        fun deleteAllProjects() =
            projectDao.deleteAllProjects()
}