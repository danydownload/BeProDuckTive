package com.example.beproducktive.ui.addedittasks

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.beproducktive.R
import com.example.beproducktive.data.tasks.TaskPriority
import com.example.beproducktive.databinding.FragmentAddEditBinding
import com.example.beproducktive.utils.exhaustive
import com.google.android.material.snackbar.Snackbar
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

                editTextTitle.addTextChangedListener {
                    viewModel.taskName = it.toString()
                }

                editTextDescription.addTextChangedListener {
                    viewModel.taskDescription = it.toString()
                }

                textViewTextDeadline.addTextChangedListener {
                    viewModel.taskDeadlineFormatted = it.toString()
                }

                spinnerTaskPriority.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedPriorityName = parent?.getItemAtPosition(position) as String
                            val selectedPriority = TaskPriority.valueOf(selectedPriorityName)
                            viewModel.taskPriority = selectedPriority
                            // You can access the selected priority here and perform any necessary actions
                            Log.d("Selected Priority", selectedPriority.name)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            // Handle the case when nothing is selected
                        }
                    }

                spinnerProject.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val selectedProjectName = parent?.getItemAtPosition(position) as String
                            viewModel.taskProject = selectedProjectName
                            // You can access the selected project name here and perform any necessary actions
                            Log.d("Selected Project", selectedProjectName)
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            // Handle the case when nothing is selected
                        }
                    }

                fabAddTask.setOnClickListener {
                    viewModel.onSaveClick()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect { event ->
                when (event) {
                    is AddEditViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        binding.editTextTitle.clearFocus()
                        setFragmentResult(
                            "add_edit_request",
                            Bundle().apply {
                                putInt("add_edit_result", event.result)
                            }
                        )
                        findNavController().popBackStack()
                    }
                }.exhaustive
            }
        }
    }
}