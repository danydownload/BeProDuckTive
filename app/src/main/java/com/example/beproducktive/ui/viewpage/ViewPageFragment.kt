package com.example.beproducktive.ui.viewpage

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.beproducktive.R
import com.example.beproducktive.databinding.FragmentTasksBinding
import com.example.beproducktive.databinding.FragmentViewPageBinding
import com.example.beproducktive.ui.tasks.TasksFragment
import dagger.hilt.android.AndroidEntryPoint

/*

@AndroidEntryPoint
class ViewPageFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_view_page, container, false)

        val binding =  FragmentViewPageBinding.bind(view)


        val fragmentList = arrayListOf<Fragment>(
            TasksFragment(),
            TasksFragment(),
            TasksFragment(),
            TasksFragment()
        )

        val viewPageAdapter = ViewPageAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager2.adapter = viewPageAdapter


        return view
    }

}

*/