package com.example.beproducktive.ui.deleteallcompleted

import androidx.lifecycle.ViewModel
import com.example.beproducktive.data.tasks.TaskRepository
import com.example.beproducktive.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteAllCompletedViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onConfirmClick() = applicationScope.launch {
        taskRepository.deleteCompletedTasks()
    }

}