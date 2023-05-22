package com.example.beproducktive.ui.addedittasks

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.beproducktive.R
import com.example.beproducktive.data.tasks.Task
import com.example.beproducktive.data.tasks.TaskPriority
import com.example.beproducktive.databinding.FragmentAddEditBinding
import com.example.beproducktive.ui.tasks.TasksFragmentArgs
import com.example.beproducktive.utils.Converters
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

enum class TaskSource {
    FROM_DAILY_VIEW,
    FROM_TASK_VIEW
}


@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit) {

    private val viewModel: AddEditViewModel by viewModels()
    private var projectNames: List<String> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditBinding.bind(view)


        binding.apply {

            textViewTextDeadline.setOnClickListener {
                // Get the current date for the DatePickerDialog initial date
                val currentDate = Calendar.getInstance()
                val currentYear = currentDate.get(Calendar.YEAR)
                val currentMonth = currentDate.get(Calendar.MONTH)
                val currentDayOfMonth = currentDate.get(Calendar.DAY_OF_MONTH)

                // Create a DatePickerDialog with the current date as the initial date
                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, year, monthOfYear, dayOfMonth ->
                        // Set the selected date as the text of the deadline TextView
                        val formattedDate =
                            String.format("%02d-%02d-%04d", dayOfMonth, monthOfYear + 1, year)
                        textViewTextDeadline.text = formattedDate
                    },
                    currentYear,
                    currentMonth,
                    currentDayOfMonth
                )

                // Show the DatePickerDialog
                datePickerDialog.show()
            }

            // Create an array of strings for the spinner's items representing the priority
            val priorityValues = TaskPriority.values().map { it.name }
            val sp1Adapter = CustomArrayAdapter(
                requireContext(), priorityValues
            )
            spinnerTaskPriority.adapter = sp1Adapter


            viewModel.projects.observe(viewLifecycleOwner) { projects ->
                projectNames = projects.map { it.projectName }
                val sp2Adapter = CustomArrayAdapter(
                    requireContext(),
                    projectNames
                )

                spinnerProject.adapter = sp2Adapter

                val index = projectNames.indexOf(viewModel.taskProject)
                if (index >= 0) {
                    spinnerProject.setSelection(index)
                }


                editTextTitle.setText(viewModel.taskName)
                editTextDescription.setText(viewModel.taskDescription)
                textViewTextDeadline.text = viewModel.taskDeadlineFormatted
                val priorityOrdinal = viewModel.task?.priority?.ordinal ?: 0
                spinnerTaskPriority.setSelection(priorityOrdinal)

                fabAddTask.setOnClickListener {
                    val newTitle = editTextTitle.text.toString()
                    val newDescription = editTextDescription.text?.toString() ?: ""
                    val newDeadline = textViewTextDeadline.text.toString()
                    val newPriority = spinnerTaskPriority.selectedItem.toString()
                    val newProject = spinnerProject.selectedItem.toString()

                    val newTask = Task(
                        taskTitle = newTitle,
                        completed = viewModel.task?.completed ?: false,
                        priority = TaskPriority.valueOf(newPriority),
                        deadline = Converters.stringToDate(newDeadline),
                        description = newDescription,
                        belongsToProject = newProject
                    )

                    if (viewModel.task != null) {
                        // Existing task, update it
                        newTask.taskId = viewModel.task!!.taskId
                        viewModel.editTask(newTask)
                    } else {
                        // New task, add it
                        viewModel.addTask(newTask)
                    }

                    // Navigate back to the previous fragment
                    findNavController().popBackStack()


                }


            }
        }
    }
}