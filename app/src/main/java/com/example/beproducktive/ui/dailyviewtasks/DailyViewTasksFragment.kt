package com.example.beproducktive.ui.dailyviewtasks

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beproducktive.R
import com.example.beproducktive.data.calendar.MyCalendar
import com.example.beproducktive.data.calendar.MyCalendarData
import com.example.beproducktive.databinding.FragmentDailyViewTasksBinding
import com.example.beproducktive.ui.addedittasks.AddEditViewModel
import com.example.beproducktive.ui.addedittasks.TaskSource
import com.example.beproducktive.ui.tasks.TasksAdapter
import com.example.beproducktive.ui.tasks.TasksFragmentDirections
import com.example.beproducktive.ui.tasks.TasksViewModel
import com.example.beproducktive.utils.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlin.collections.ArrayList

@AndroidEntryPoint
class DailyViewTasksFragment : Fragment(R.layout.fragment_daily_view_tasks) {

    private val viewModel: TasksViewModel by viewModels()

    private var calendarList = ArrayList<MyCalendar>()
    private lateinit var mAdapter: DailyViewTasksAdapter
    private var currentPosition = 0

    private lateinit var recyclerViewTasks2: RecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDailyViewTasksBinding.bind(view)

        val taskAdapter = TasksAdapter(TasksAdapter.OnClickListener { task ->
            Toast.makeText(requireContext(), task.taskTitle, Toast.LENGTH_SHORT).show()

            Log.d("TASK-SELECTED", "task: $task")
            viewModel.onTaskSelected(task.belongsToProject, task)

        }, TasksAdapter.OnTimerClickListener { task ->
            viewModel.onTimerSelected(task)
        })

        recyclerViewTasks2 = binding.recyclerViewTasks2

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
                    recyclerViewTasks: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val task = taskAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onTaskSwiped(task)
                }
            }).attachToRecyclerView(recyclerViewTasks)

            mAdapter = DailyViewTasksAdapter(DailyViewTasksAdapter.OnClickListener { calendar ->

//                Log.d("CLICKED-DATE", "${calendar.getSelectedDate()} is clicked!")
//                Log.d("CLICKED-DATE", "calendarList: ${calendarList}")

                calendar.isSelected = true

                val childTextView = view.findViewById<TextView>(R.id.date_1)
                val startRotateAnimation: Animation =
                    AnimationUtils.makeInChildBottomAnimation(requireContext())
                childTextView.startAnimation(startRotateAnimation)
                childTextView.setTextColor(Color.CYAN)

                Toast.makeText(
                    requireContext(),
                    "${calendar.getSelectedDate()} is selected!",
                    Toast.LENGTH_SHORT
                ).show()

                mAdapter.notifyItemChanged(calendarList.indexOf(calendar))

                val dateSelected = calendar.getSelectedDate()
                Log.d("SELECTED-DATE","$dateSelected is selected!")

                // when a day is selected, all the other are unselected
                for (i in calendarList.indices) {
                    if (i != calendarList.indexOf(calendar)) {
                        calendarList[i].isSelected = false
                        mAdapter.notifyItemChanged(i)
                    }
                }

                viewModel.onDayClicked(dateSelected!!)

            })

            mLayoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            recyclerViewTasks2.apply {
                adapter = mAdapter
                layoutManager = mLayoutManager
                setHasFixedSize(true)
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL
                    )
                )
                itemAnimator = DefaultItemAnimator()
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        val totalItemCount = mLayoutManager.childCount
                        for (i in 0 until totalItemCount) {
                            val childView: View = recyclerViewTasks2.getChildAt(i)
                            val childTextView: TextView = childView.findViewById(R.id.day_1)
                            // setting the day of the week
                            val childTextViewText: String = childTextView.text.toString()
                            if (childTextViewText.equals(
                                    "Sun",
                                    ignoreCase = true
                                ) or childTextViewText.equals(
                                    "Dom",
                                    ignoreCase = true
                                )
                            ) {
                                childTextView.setTextColor(Color.RED)
                            } else {
                                childTextView.setTextColor(Color.BLACK)
                            }
                        }
                    }
                })


                prepareCalendarData()
                mAdapter.submitList(calendarList)

            }

            val calendar = MyCalendar()
            val currentDate = calendar.getCurrentDate()
            println("Current date: $currentDate")

            viewModel.getTasksForDate(currentDate).observe(viewLifecycleOwner) { tasksList ->
                taskAdapter.submitList(tasksList)
            }

            fabAddTask.setOnClickListener {
                viewModel.onclickAddTask()
            }

            setFragmentResultListener("add_edit_request") { _, bundle ->
                Log.d("TasksFragment", "add_edit_request")
                val result = bundle.getInt("add_edit_result")
                viewModel.onAddEditResult(result)
            }

        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect { event ->
                when (event) {
                    TasksViewModel.TasksEvent.NavigateToAddTaskScreen -> {
                        val action = DailyViewTasksFragmentDirections.actionDailyViewTasksFragmentToAddEditFragment(projectName = null, task = null, taskSource = TaskSource.FROM_DAILY_VIEW)
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.NavigateToEditTaskScreen -> {
                        val action = DailyViewTasksFragmentDirections.actionDailyViewTasksFragmentToAddEditFragment(projectName = event.projectName, task = event.task, taskSource = TaskSource.FROM_DAILY_VIEW)
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.NavigateToTimerFragment -> {
                        val action = DailyViewTasksFragmentDirections.actionDailyViewTasksFragmentToTimerFragment(event.task)
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }
                    is TasksViewModel.TasksEvent.RefreshTasks -> {
                        viewModel.getTasksForDate(event.dateSelected).observe(viewLifecycleOwner) { tasksList ->
                            taskAdapter.submitList(tasksList)
                        }
                    }
                    TasksViewModel.TasksEvent.NavigateToProjectScreen -> {}
                    is TasksViewModel.TasksEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClick(event.task)
                            }.show()
                    }
                }
            }
        }
    }

    private fun getCurrentItem(): Int {
        return (recyclerViewTasks2.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
    }

    private fun setCurrentItem(position: Int, incr: Int) {
        var newPosition = position + incr

        if (newPosition < 0) {
            newPosition = 0
        }

        recyclerViewTasks2.smoothScrollToPosition(newPosition)
    }

    /**
     * Prepares sample data to provide data set to adapter
     */
    private fun prepareCalendarData() {
        // run a for loop for all the next 30 days of the current month starting today
        // initialize mycalendarData and get Instance
        // getnext to get next set of date etc.. which can be used to populate MyCalendarList in for loop

        // if the calendar is not empty that means that it is already populated
        if (calendarList.isNotEmpty()) {
            return
        }


        val mCalendar = MyCalendarData(-2)
        // just create 30 days forward from today
        for (i in 0 until 30) {
            val calendar = MyCalendar(
                mCalendar.getWeekDay(),
                mCalendar.getDay().toString(),
                mCalendar.getMonth().toString(),
                mCalendar.getYear().toString(),
                i
            )
            mCalendar.getNextWeekDay(1)

            calendarList.add(calendar)

        }

        Log.d("CALENDAR-SIZE", "Size of calendarList: ${calendarList.size}")

        // notify adapter about data set changes
        // so that it will render the list with new data

        mAdapter.notifyDataSetChanged()

    }

}