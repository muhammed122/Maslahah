package com.example.maslahah.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.maslahah.R
import com.example.maslahah.databinding.FragmentTasksScreenBinding


class TasksScreenFragment : Fragment() {


    private var _binding: FragmentTasksScreenBinding? = null
    private val binding get() = _binding!!

    var value = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        value = TasksScreenFragmentArgs.fromBundle(
            requireArguments()
        ).value

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tasks_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTasksScreenBinding.bind(view)
        binding.tasksValue.text = value.toString()
    }

}