package com.example.beproducktive.data.tasks

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.beproducktive.data.projects.Project
import com.example.beproducktive.data.projects.ProjectDao
import junit.framework.TestCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsCollectionContaining
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TaskDatabaseTest : TestCase() {

    private lateinit var projectDao: ProjectDao
    private lateinit var taskDao: TaskDao
    private lateinit var db: TaskDatabase

    @Before
    public override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, TaskDatabase::class.java).build()
        projectDao = db.projectDao()
        taskDao = db.taskDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeProjectAndTasksAndRead() : Unit = runBlocking {
        val project = Project("Personal")
        val project2 = Project("Other Things")
        val task = Task("fix code", false, TaskPriority.HIGH, "Personal")
        val task2 = Task("FOLLOW THE INDIAN GUY", true, TaskPriority.HIGH, "Other Things")
        val task3 = Task("MAKE A BREAK", false, TaskPriority.LOW, "Other Things")

        projectDao.insert(project)
        projectDao.insert(project2)
        taskDao.insert(task)
        taskDao.insert(task2)
        taskDao.insert(task3)

        val testProjectAndTasks = projectDao.getProjectWithTasks().first()
        for (pair in testProjectAndTasks) {
            val project = pair.project
            val tasks = pair.tasks
            Log.d("DBTEST", "Project: ${project.projectName}")
            for (task in tasks) {
                Log.d("DBTEST", "Task: ${task.taskTitle}")
            }
        }

        val p = projectDao.getByProjectName(project2.projectName).first()
        for (pair in p[0].tasks)
        {
            val taskName = pair.taskTitle
            Log.d("TAGSD", taskName)
        }


        val projectsAndTasks = projectDao.getProjectWithTasks().first()
        MatcherAssert.assertThat(projectsAndTasks.size, IsEqual.equalTo(2))

        val projectAndTasks = projectsAndTasks[0]
        MatcherAssert.assertThat(projectAndTasks.project.projectName, IsEqual.equalTo("Personal"))
        MatcherAssert.assertThat(projectAndTasks.tasks.size, IsEqual.equalTo(1))

        val insertedTask = projectAndTasks.tasks[0]
        MatcherAssert.assertThat(insertedTask.taskTitle, IsEqual.equalTo("fix code"))
        MatcherAssert.assertThat(insertedTask.priority, IsEqual.equalTo(TaskPriority.HIGH))


        val projectAndTasks2 = projectsAndTasks[1]
        Log.d("TASKS", projectAndTasks2.project.projectName)
        Log.d("TASKS", projectAndTasks2.tasks[0].taskTitle)
        Log.d("TASKS", projectAndTasks2.tasks[1].taskTitle)

        MatcherAssert.assertThat(
            projectAndTasks2.project.projectName,
            IsEqual.equalTo("Other Things")
        )
        MatcherAssert.assertThat(projectAndTasks2.tasks.size, IsEqual.equalTo(2))

        val insertedTask21 = projectAndTasks2.tasks[0]
        MatcherAssert.assertThat(insertedTask21.taskTitle, IsEqual.equalTo("FOLLOW THE INDIAN GUY"))
        MatcherAssert.assertThat(insertedTask21.priority, IsEqual.equalTo(TaskPriority.HIGH))

        val insertedTask22 = projectAndTasks2.tasks[1]
        MatcherAssert.assertThat(insertedTask22.taskTitle, IsEqual.equalTo("MAKE A BREAK"))
        MatcherAssert.assertThat(insertedTask22.priority, IsEqual.equalTo(TaskPriority.LOW))

    }

}