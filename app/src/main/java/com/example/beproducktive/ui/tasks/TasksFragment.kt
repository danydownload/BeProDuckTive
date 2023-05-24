package com.example.beproducktive.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beproducktive.R
import com.example.beproducktive.data.SortOrder
import com.example.beproducktive.databinding.FragmentTasksBinding
import com.example.beproducktive.ui.addedittasks.TaskSource
import com.example.beproducktive.utils.exhaustive
import com.example.beproducktive.utils.onQueryTextChanged
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: TasksViewModel by viewModels()

    private lateinit var menuProvider: MenuProvider


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()

        val binding = FragmentTasksBinding.bind(view)

        val taskAdapter = TasksAdapter(TasksAdapter.OnClickListener { task ->
            Toast.makeText(requireContext(), task.taskTitle, Toast.LENGTH_SHORT).show()
            viewModel.onTaskSelected(viewModel.projectName, task)
        }, TasksAdapter.OnTimerClickListener { task ->
            viewModel.onTimerSelected(task)
        }, TasksAdapter.OnCheckboxClickListener { task, isChecked ->
            viewModel.onCheckboxSelected(task, isChecked)
        })

        binding.apply {
            recyclerViewTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {

                override fun onMove(
                    recyclerView: androidx.recyclerview.widget.RecyclerView,
                    viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                    target: androidx.recyclerview.widget.RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(
                    viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val task = taskAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(task)
                }
            }).attachToRecyclerView(recyclerViewTasks)

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
//                        val list = viewModel.onReceiveProject(viewModel.projectName)
//                        list.observe(viewLifecycleOwner) { taskList ->
//                            taskAdapter.submitList(taskList)
//                        }
                        viewModel.allTasks.observe(viewLifecycleOwner) { listTasks ->
                            taskAdapter.submitList(listTasks)
                        }
                    }
                    textviewProjectName.text = viewModel.projectName.uppercase()
                }
            } else {
                textviewProjectName.text = viewModel.projectName.uppercase()
//                val list = viewModel.onReceiveProject(viewModel.projectName)
//                list.observe(viewLifecycleOwner) { taskList ->
//                    taskAdapter.submitList(taskList)
//                }
                viewModel.allTasks.observe(viewLifecycleOwner) { listTasks ->
                    taskAdapter.submitList(listTasks)
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
                    is TasksViewModel.TasksEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClick(event.task)
                            }.show()
                    }
                }.exhaustive // to check that all cases are covered. It's a compiler check (compile safety)
            }
        }
    }


    private fun setupMenu() {
        val currentMenuProvider = object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_fragment_tasks, menu)

                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView

                searchView.onQueryTextChanged {
                    viewModel.searchQuery.value = it
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    menu.findItem(R.id.action_hide_completed_tasks).isChecked =
                            viewModel.preferencesFlow.first().hideCompleted
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when (menuItem.itemId)
                {
                    R.id.action_sort_by_name -> {
                        viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                        return true
                    }
                    R.id.action_sort_by_deadline -> {
                        viewModel.onSortOrderSelected(SortOrder.BY_DEADLINE)
                        return true
                    }
                    R.id.action_hide_completed_tasks -> {
                        menuItem.isChecked = !menuItem.isChecked
                        viewModel.onHideCompletedClick(menuItem.isChecked)
                        return true
                    }
                    R.id.action_delete_all_tasks -> {
                        return true
                    }
                    else -> return false
                }
            }
        }
        menuProvider = currentMenuProvider
        (requireActivity() as MenuHost).addMenuProvider(currentMenuProvider)
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as MenuHost).removeMenuProvider(menuProvider)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (requireActivity() as MenuHost).removeMenuProvider(menuProvider)
    }



}