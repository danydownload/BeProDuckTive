package com.example.beproducktive.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH
}

@Entity(tableName = "task_table")
@kotlinx.parcelize.Parcelize
data class Task(
    val taskTitle: String,
    val completed: Boolean = false,
    val priority: TaskPriority,
    val deadline: String = SimpleDateFormat("dd-MM-yyyy").format(Date()),
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) : Parcelable {
    val deadlineFormatted: String
        get() = deadline
}
