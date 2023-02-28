package com.example.beproducktive.data.tasks

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.beproducktive.data.projects.Project
import com.example.beproducktive.data.projects.ProjectDao
import com.example.beproducktive.di.ApplicationScope
import com.example.beproducktive.utils.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Provider

//@Database(entities = [Project::class, Task::class], version = 3,  exportSchema = false)
@Database(entities = [Project::class, Task::class],
    version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun projectDao(): ProjectDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            applicationScope.launch {

                val projectDao = database.get().projectDao()

                val project1 = Project("Personal")
                val project2 = Project("Other Things")
                val project3 = Project("Work")

                projectDao.insert(project1)
                projectDao.insert(project2)
                projectDao.insert(project3)

                val task1 = Task("Do laundry", false, TaskPriority.HIGH, "Other Things")
                val task2 = Task("Buy groceries", false, TaskPriority.MEDIUM, "Other Things")
                val task3 = Task(
                    "Clean the house",
                    true,
                    TaskPriority.LOW,
                    "Other Things",
                    SimpleDateFormat("dd-MM-yyyy", Locale.ITALIAN).parse("10-02-2023")
                )
                val task4 = Task(
                    "Finish report",
                    false,
                    TaskPriority.LOW,
                    "Personal",
                    SimpleDateFormat("dd-MM-yyyy", Locale.ITALIAN).parse("27-03-2023")
                )
                val task5 = Task("Meet with client", true, TaskPriority.MEDIUM, "Personal", description = "American diner at 9.00pm")
                val task6 = Task("Attend workshop", false, TaskPriority.HIGH, "Personal", description = "Argument: AI, ML and DL")
                val task7 = Task("Go to the gym", false, TaskPriority.LOW, "Personal", description = "Leg-day")
                val task8 = Task("Call mom", false, TaskPriority.HIGH, "Other Things")
                val task9 = Task("Buy birthday gift", false, TaskPriority.MEDIUM, "Other Things")
                val task10 = Task("Book flight tickets", true, TaskPriority.LOW, "Personal", description = "Destination: NY")

                val taskDao = database.get().taskDao()
                taskDao.insert(task1)
                taskDao.insert(task2)
                taskDao.insert(task3)
                taskDao.insert(task4)
                taskDao.insert(task5)
                taskDao.insert(task6)
                taskDao.insert(task7)
                taskDao.insert(task8)
                taskDao.insert(task9)
                taskDao.insert(task10)

            }
        }

    }
}
