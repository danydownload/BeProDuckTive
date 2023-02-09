package com.example.beproducktive.data.projects

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "project_table")
@Parcelize
data class Project(
    @PrimaryKey() val projectName: String,
) : Parcelable