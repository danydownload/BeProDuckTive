package com.example.beproducktive.ui.dailyviewtasks

import android.graphics.Color
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.support.v4.os.IResultReceiver.Default
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.AbsListView.RecyclerListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beproducktive.R
import com.example.beproducktive.data.calendar.MyCalendar
import com.example.beproducktive.data.calendar.MyCalendarData
import com.example.beproducktive.databinding.FragmentDailyViewTasksBinding
import com.example.beproducktive.ui.addedittasks.TaskSource
import com.example.beproducktive.ui.tasks.TasksAdapter
import com.example.beproducktive.ui.tasks.TasksFragmentDirections
import com.example.beproducktive.ui.tasks.TasksViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
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

            viewModel.getProjectNameForTask(task.taskId).observe(viewLifecycleOwner) { projectName ->
                findNavController().navigate(DailyViewTasksFragmentDirections.actionDailyViewTasksFragmentToAddEditFragment(projectName = projectName!!, task = task, taskSource = TaskSource.FROM_DAILY_VIEW))
            }


        })

        recyclerViewTasks2 = binding.recyclerViewTasks2


        binding.apply {
            recyclerViewTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }


            mAdapter = DailyViewTasksAdapter(DailyViewTasksAdapter.OnClickListener { calendar ->

                Log.d("CLICKED-DATE", "${calendar.getSelectedDate()} is clicked!")
                Log.d("CLICKED-DATE", "calendarList: ${calendarList}")

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

                // when a day is selected, the tasks for that day are displayed
                viewModel.getTasksForDate(dateSelected).observe(viewLifecycleOwner) { task ->
                    taskAdapter.submitList(task)
                }



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

//            fabAddTask.setOnClickListener {
//                findNavController().navigate(TasksFragmentDirections.actionTasksFragmentToAddEditTaskFragment())
//            }

            // create an instance of MyCalendar and call getCurrentDate method
            val calendar = MyCalendar()
            val currentDate = calendar.getCurrentDate()
            println("Current date: $currentDate")

            viewModel.getTasksForDate(currentDate).observe(viewLifecycleOwner) { tasksList ->
                taskAdapter.submitList(tasksList)
            }

            // TODO FIX THIS: add direction to navigate to AddEditFragment
            fabAddTask.setOnClickListener {
//                findNavController().navigate(TasksFragmentDirections.actionTasksFragmentToAddEditFragment("", null, TaskSource.FROM_DAILY_VIEW))
                findNavController().navigate(DailyViewTasksFragmentDirections.actionDailyViewTasksFragmentToTasksFragment())
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