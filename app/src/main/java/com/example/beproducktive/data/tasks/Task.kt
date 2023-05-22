package com.example.beproducktive.data.tasks

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.beproducktive.utils.Converters
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}

@Entity(tableName = "task_table")
@TypeConverters(Converters::class)
@Parcelize
data class Task(
    val taskTitle: String,
    val completed: Boolean = false,
    val priority: TaskPriority,
    val belongsToProject: String,
    val deadline: Date? = Date(),
    val description: String? = null,
    @PrimaryKey(autoGenerate = true) var taskId: Int = 0
) : Parcelable {
    val deadlineFormatted: String
        get() = SimpleDateFormat("dd-MM-yyyy", Locale.ITALIAN).format(deadline!!)
}