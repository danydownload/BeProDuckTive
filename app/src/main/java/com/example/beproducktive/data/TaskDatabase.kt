package com.example.beproducktive.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.beproducktive.di.ApplicationScope
import com.example.beproducktive.utils.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
@TypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()

            applicationScope.launch {
                val task1 = Task("Do laundry", false, TaskPriority.HIGH)
                val task2 = Task("Buy groceries", false, TaskPriority.MEDIUM)
                val task3 = Task("Clean the house", true, TaskPriority.LOW)
                val task4 = Task("Finish report", false, TaskPriority.LOW)
                val task5 = Task("Meet with client", true, TaskPriority.MEDIUM)
                val task6 = Task("Attend workshop", false, TaskPriority.HIGH)
                val task7 = Task("Go to the gym", false, TaskPriority.LOW)
                val task8 = Task("Call mom", false, TaskPriority.HIGH)
                val task9 = Task("Buy birthday gift", false, TaskPriority.MEDIUM)
                val task10 = Task("Book flight tickets", true, TaskPriority.LOW)

                val dao = database.get().taskDao()
                dao.insert(task1)
                dao.insert(task2)
                dao.insert(task3)
                dao.insert(task4)
                dao.insert(task5)
                dao.insert(task6)
                dao.insert(task7)
                dao.insert(task8)
                dao.insert(task9)
                dao.insert(task10)

            }
        }

    }
}