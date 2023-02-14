package com.example.beproducktive.ui.addedittasks

import androidx.lifecycle.ViewModel
import com.example.beproducktive.data.tasks.TaskDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val taskDao: TaskDao
) : ViewModel() {
    // TODO: Implement the ViewModel
}