package com.example.beproducktive.ui.projects

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beproducktive.R
import com.example.beproducktive.databinding.FragmentProjectsBinding
import com.example.beproducktive.utils.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ProjectsFragment : Fragment(R.layout.fragment_projects) {

    private val viewModel: ProjectsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProjectsBinding.bind(view)

        val projectAdapter = ProjectsAdapter(ProjectsAdapter.OnClickListener { project ->
//            Log.d("ONCLICK", "CLICKED PROJECT")
            Toast.makeText(requireContext(), project.projectName, Toast.LENGTH_SHORT).show()
            findNavController().navigate(
                ProjectsFragmentDirections.actionProjectsFragmentToTasksFragment(
                    project
                )
            )

        })


        binding.apply {
            recyclerViewProjects.apply {
                adapter = projectAdapter
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
                    val project = projectAdapter.currentList[viewHolder.adapterPosition]
                    viewModel.onProjectSwiped(project)
                }

            }).attachToRecyclerView(recyclerViewProjects)


            editNewProject.addTextChangedListener {
                viewModel.projName = it.toString()
            }

            buttonNewProject.setOnClickListener {
                viewModel.onClickNewProject(viewModel.projName)
            }

        }

        viewModel.projects.observe(viewLifecycleOwner) { projectsList ->
            projectAdapter.submitList(projectsList)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addProjectEvent.collect { event ->
                when (event) {
                    is ProjectsViewModel.ProjectsEvent.AddProject -> {
                        binding.editNewProject.clearFocus()
                        // navigate to the same fragment with navcontroller to refresh the list
                        val action = ProjectsFragmentDirections.actionProjectsFragmentSelf()
                        findNavController().navigate(action)
                        Snackbar.make(requireView(), "Project added", Snackbar.LENGTH_SHORT).show()
                    }

                    is ProjectsViewModel.ProjectsEvent.ShowInvalidInputMessage -> {
                        binding.editNewProject.clearFocus()
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                    }

                    is ProjectsViewModel.ProjectsEvent.ShowUndoDeleteProjectMessage -> {
                        Snackbar.make(requireView(), "Project deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClick(event.project)
                            }.show()
                    }
                }.exhaustive
            }

        }

    }
}