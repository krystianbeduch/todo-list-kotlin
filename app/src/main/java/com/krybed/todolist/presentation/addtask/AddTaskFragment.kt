package com.krybed.todolist.presentation.addtask;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.krybed.todolist.databinding.FragmentTaskFormBinding
import com.krybed.todolist.util.task.TaskFormHelper

class AddTaskFragment : Fragment() {

    private var binding: FragmentTaskFormBinding? = null
    private val b get() = binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskFormBinding.inflate(
            inflater,
            container,
            false
        )

        val helper = TaskFormHelper(
                requireContext(),
                b.taskTitle,
                b.taskDeadline,
                b.taskPriority
        )
        b.taskSaveButton.setOnClickListener {
            helper.handleSave(
                callback = {
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                },
                isEditMode = false,
                existingTask = null
            )
        }
        return b.root;
    }

    override fun onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}