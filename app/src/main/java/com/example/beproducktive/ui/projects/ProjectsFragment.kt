package com.example.beproducktive.ui.projects

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beproducktive.R
import com.example.beproducktive.databinding.FragmentProjectsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProjectsFragment : Fragment(R.layout.fragment_projects) {

    private val viewModel: ProjectsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentProjectsBinding.bind(view)

        val projectAdapter = ProjectsAdapter(ProjectsAdapter.OnClickListener {project ->
//            Log.d("ONCLICK", "CLICKED PROJECT")
            Toast.makeText(requireContext(), project.projectName, Toast.LENGTH_SHORT).show()
            findNavController().navigate(ProjectsFragmentDirections.actionProjectsFragmentToTasksFragment(project))

        })


        binding.apply {
            recyclerViewProjects.apply {
                adapter = projectAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            buttonNewProject.setOnClickListener {
//                findNavController().navigate(R.id.action_projectsFragment_to_addProjectFragment)
//                val AddProjectFragment = AddProjectFragment()
//                AddProjectFragment.show(requireActivity().supportFragmentManager, "AddProjectFragment")
            }

        }

        viewModel.projects.observe(viewLifecycleOwner) { projectsList ->
            projectAdapter.submitList(projectsList)
        }

    }
}