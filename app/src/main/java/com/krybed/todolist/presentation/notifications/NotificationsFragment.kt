package com.krybed.todolist.presentation.notifications;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.krybed.todolist.databinding.FragmentNotificationsBinding
import com.krybed.todolist.presentation.viewmodel.TaskViewModel

class NotificationsFragment : Fragment() {

    private var binding: FragmentNotificationsBinding? = null
    private val b get() = binding!!

    private lateinit var notificationAdapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationsBinding.inflate(
            inflater,
            container,
            false
        )
        notificationAdapter = NotificationAdapter(requireContext(), emptyList())
        b.tasksRecyclerView.setLayoutManager(LinearLayoutManager(context))
        b.tasksRecyclerView.setAdapter(notificationAdapter)

        val taskViewModel: TaskViewModel =
            ViewModelProvider(requireActivity())[TaskViewModel::class.java]
        taskViewModel.tasksForNotification.observe(viewLifecycleOwner) { tasks ->
            notificationAdapter.updateTasks(tasks)
//            notificationAdapter.setTasks(tasks
        }
        return b.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}