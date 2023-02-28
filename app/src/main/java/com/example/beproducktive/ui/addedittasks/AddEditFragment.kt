package com.example.beproducktive.ui.addedittasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.beproducktive.R
import com.example.beproducktive.data.tasks.TaskPriority
import com.example.beproducktive.databinding.FragmentAddEditBinding
import com.example.beproducktive.ui.tasks.TasksFragmentArgs
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditFragment : Fragment(R.layout.fragment_add_edit) {

    private val viewModel: AddEditViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditBinding.bind(view)

        val bundle = arguments
        if (bundle == null) {
            Log.e("Tasks", "AddEditTasksFragment did not receive task information")
        }
        else {
            val args = AddEditFragmentArgs.fromBundle(bundle)
            args.task?.let { task ->
                Log.d("CIAO-PP", task.taskTitle)

                binding.apply {

                    editTextTitle.setText(task.taskTitle)
                    editTextDescription.setText(task.description)
                    textViewTextDeadline.text = task.deadlineFormatted

                    if (task.priority == TaskPriority.HIGH) {
                        buttonPriorityHigh.requestFocus()
                    }
                    else if (task.priority == TaskPriority.MEDIUM) {
                        buttonPriorityMedium.requestFocus()
                    }
                    else {
                        buttonPriorityLow.requestFocus()
                    }

                    textViewTextProject.text = args.projectName
                }

            }



        }
    }


}