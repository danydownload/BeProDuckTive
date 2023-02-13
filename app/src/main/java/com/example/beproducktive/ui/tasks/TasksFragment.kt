package com.example.beproducktive.ui.tasks

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beproducktive.R
import com.example.beproducktive.databinding.FragmentTasksBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: TasksViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding =  FragmentTasksBinding.bind(view)

        val taskAdapter = TasksAdapter(TasksAdapter.OnClickListener { task ->
//            Log.d("ONCLICK", "CLICKED TASK")
            Toast.makeText(requireContext(), task.taskTitle, Toast.LENGTH_SHORT).show()
        })


        binding.apply {
            recyclerViewTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            cardView.setOnClickListener {
                viewModel.onclickProject(findNavController())
            }
        }

        val bundle = arguments
        if (bundle == null) {
            Log.e("Tasks", "TasksFragment did not receive project information")
            return
        }

        val args = TasksFragmentArgs.fromBundle(bundle)
//        args.project?.let { Log.d("ONCLICK-R", it.projectName) }
        if (args.project != null) {
            val list = viewModel.onReceiveProject(args.project.projectName)
            list.observe(viewLifecycleOwner) {
                taskAdapter.submitList(it)
            }
        }
        else {
            viewModel.tasks.observe(viewLifecycleOwner) { tasksList ->
                taskAdapter.submitList(tasksList)
            }
        }

        viewModel.projects.observe(viewLifecycleOwner) { projectsList ->
            binding.apply {
                textviewProjectName.text = "Project: ${projectsList[0].projectName}".uppercase()
                fabAddTask.setColorFilter(ContextCompat.getColor(view.context, R.color.blue_gray));
            }
        }





    }
}