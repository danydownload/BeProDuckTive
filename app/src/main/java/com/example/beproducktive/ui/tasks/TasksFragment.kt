package com.example.beproducktive.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beproducktive.R
import com.example.beproducktive.databinding.FragmentTasksBinding
import com.example.beproducktive.databinding.ItemTaskBinding
import com.example.beproducktive.ui.addedittasks.TaskSource
import com.example.beproducktive.utils.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: TasksViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTasksBinding.bind(view)

        val taskAdapter = TasksAdapter(TasksAdapter.OnClickListener { task ->
            Toast.makeText(requireContext(), task.taskTitle, Toast.LENGTH_SHORT).show()
            viewModel.onTaskSelected(viewModel.projectName, task)
        }, TasksAdapter.OnTimerClickListener { task ->
            viewModel.onTimerSelected(task)
        })

        binding.apply {
            recyclerViewTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            cardView.setOnClickListener {
                viewModel.onProjectClicked()
            }

            fabAddTask.setOnClickListener {
                viewModel.onClickAddTask()
            }


            fabAddTask.setColorFilter(ContextCompat.getColor(view.context, R.color.blue_gray))

            setFragmentResultListener("add_edit_request") { _, bundle ->
                Log.d("TasksFragment", "add_edit_request")
                val result = bundle.getInt("add_edit_result")
                viewModel.onAddEditResult(result)
            }

            Log.d("TasksFragment", "Project: ${viewModel.project}")
            Log.d("TasksFragment", "PName: ${viewModel.projectName}")

            if (viewModel.projectName.isEmpty()) {
                viewModel.firstProject.observe(viewLifecycleOwner) { project ->
                    project?.let {
                        viewModel.projectName = it.projectName
                        val list = viewModel.onReceiveProject(viewModel.projectName)
                        list.observe(viewLifecycleOwner) { taskList ->
                            taskAdapter.submitList(taskList)
                        }
                    }
                    textviewProjectName.text = viewModel.projectName.uppercase()
                }
            } else {
                textviewProjectName.text = viewModel.projectName.uppercase()
                val list = viewModel.onReceiveProject(viewModel.projectName)
                list.observe(viewLifecycleOwner) { taskList ->
                    taskAdapter.submitList(taskList)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect() { event ->
                when (event) {
                    is TasksViewModel.TasksEvent.NavigateToAddTaskScreen -> {
                        val action = TasksFragmentDirections.actionTasksFragmentToAddEditFragment(
                            projectName = viewModel.projectName,
                            task = null,
                            taskSource = TaskSource.FROM_TASK_VIEW
                        )
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.NavigateToEditTaskScreen -> {
                        val action = TasksFragmentDirections.actionTasksFragmentToAddEditFragment(
                            projectName = event.projectName,
                            task = event.task,
                            taskSource = TaskSource.FROM_TASK_VIEW
                        )
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.NavigateToProjectScreen -> {
                        val action = TasksFragmentDirections.actionTasksFragmentToProjectsFragment()
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    is TasksViewModel.TasksEvent.NavigateToTimerFragment -> {
                        val action =
                            TasksFragmentDirections.actionTasksFragmentToTimerFragment(event.task)
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.RefreshTasks -> {}
                }.exhaustive // to check that all cases are covered. It's a compiler check (compile safety)
            }
        }
    }
}