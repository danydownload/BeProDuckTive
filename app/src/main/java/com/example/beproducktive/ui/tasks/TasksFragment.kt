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

    private var projectName : String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding =  FragmentTasksBinding.bind(view)


        val taskAdapter = TasksAdapter(TasksAdapter.OnClickListener { task ->
            Toast.makeText(requireContext(), task.taskTitle, Toast.LENGTH_SHORT).show()
//            findNavController().navigate(TasksFragmentDirections.actionTasksFragmentToAddEditFragment(task))
            viewModel.onTaskSelected(projectName, task)
//            findNavController().safeNavigate(TasksFragmentDirections.actionTasksFragmentToAddEditFragment(projectName = projectName, task = task, taskSource = TaskSource.FROM_TASK_VIEW))
        }, TasksAdapter.OnTimerClickListener { task ->
            viewModel.onTimerSelected(task)
//            findNavController().safeNavigate(TasksFragmentDirections.actionTasksFragmentToTimerFragment(task))
        })



        binding.apply {
            recyclerViewTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            // TODO - Handle as event
            cardView.setOnClickListener {
                viewModel.onClickProject(findNavController())
            }

            fabAddTask.setOnClickListener {
                viewModel.onClickAddTask()
//                findNavController().navigate(R.id.action_tasksFragment_to_addEditFragment)
            }

        }

        val bundle = arguments
        if (bundle == null) {
            Log.e("CIAO-P", "TasksFragment did not receive project information")


            viewModel.projects.observe(viewLifecycleOwner) { projectsList ->
                binding.apply {
                    Log.d("CIAO-P", "project0")
                    textviewProjectName.text = "Project: ${projectsList[0].projectName}".uppercase()
                    fabAddTask.setColorFilter(ContextCompat.getColor(view.context, R.color.blue_gray));
                }
            }

            setFragmentResultListener("add_edit_request") { _, bundle ->
                Log.d("TasksFragment", "add_edit_request")
                val result = bundle.getInt("add_edit_result")
                viewModel.onAddEditResult(result)
            }

            viewModel.tasks.observe(viewLifecycleOwner) { tasksList ->
                taskAdapter.submitList(tasksList)
            }

        }
        else {
            Log.e("CIAO-P", "TasksFragment receives project information")
            val args = TasksFragmentArgs.fromBundle(bundle)
            args.project?.let { Log.d("CIAO-P", it.projectName) }
            if (args.project != null) {
                val list = viewModel.onReceiveProject(args.project.projectName)

                binding.apply {
                    textviewProjectName.text = "Project: ${args.project.projectName}".uppercase()
                    projectName = args.project.projectName
                    fabAddTask.setColorFilter(ContextCompat.getColor(view.context, R.color.blue_gray));
                }

                list.observe(viewLifecycleOwner) {
                    taskAdapter.submitList(it)
                }
            } else {
                viewModel.projects.observe(viewLifecycleOwner) { projectsList ->
                    binding.apply {
//                        Log.d("CIAO-P", "project0: ${projectsList[0].projectName}")
                        if (projectsList.isEmpty())
                            textviewProjectName.text = ""
                        else {
                            textviewProjectName.text =
                                "Project: ${projectsList[0].projectName}".uppercase()
                            projectName = projectsList[0].projectName
                        }
                        fabAddTask.setColorFilter(ContextCompat.getColor(view.context, R.color.blue_gray));
                    }
                }

                setFragmentResultListener("add_edit_request") { _, bundle ->
                    Log.d("TasksFragment", "add_edit_request")
                    val result = bundle.getInt("add_edit_result")
                    viewModel.onAddEditResult(result)
                }

                viewModel.tasks.observe(viewLifecycleOwner) { tasksList ->
                    taskAdapter.submitList(tasksList)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect() { event ->
                when (event) {
                    is TasksViewModel.TasksEvent.NavigateToAddTaskScreen -> {
                        val action = TasksFragmentDirections.actionTasksFragmentToAddEditFragment(projectName = null, task = null, taskSource = TaskSource.FROM_TASK_VIEW)
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.NavigateToEditTaskScreen -> {
                        val action = TasksFragmentDirections.actionTasksFragmentToAddEditFragment(projectName = event.projectName, task = event.task, taskSource = TaskSource.FROM_TASK_VIEW)
                        findNavController().navigate(action)
                    }
//                    is TasksViewModel.TasksEvent.ShowTaskSavedConfirmationMessage -> {
//                        Toast.makeText(requireContext(), event.msg, Toast.LENGTH_SHORT).show()
//                    }
//                    is TasksViewModel.TasksEvent.NavigateToProjectScreen -> {
//                        val action = TasksFragmentDirections.actionTasksFragmentToProjectsFragment()
//                        findNavController().navigate(action)
//                    }
                    is TasksViewModel.TasksEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    is TasksViewModel.TasksEvent.NavigateToTimerFragment -> {
                        val action = TasksFragmentDirections.actionTasksFragmentToTimerFragment(event.task)
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.RefreshTasks -> {}
                }.exhaustive // to check that all cases are covered. It's a compiler check (compile safety)
            }
        }


    }

    fun NavController.safeNavigate(direction: NavDirections) {

        currentDestination?.getAction(direction.actionId)?.run {
            navigate(direction)
        }
    }
}