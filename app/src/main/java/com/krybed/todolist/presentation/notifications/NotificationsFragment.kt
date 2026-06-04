package com.krybed.todolist.presentation.notifications;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.krybed.todolist.databinding.FragmentNotificationsBinding
import com.krybed.todolist.presentation.AppContainer
import com.krybed.todolist.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.launch

class NotificationsFragment : Fragment() {

    private var binding: FragmentNotificationsBinding? = null
    private val b get() = binding!!
    private lateinit var notificationAdapter: NotificationAdapter
    private val taskViewModel: TaskViewModel by activityViewModels {
        AppContainer.provideTaskViewModelFactory(requireContext().applicationContext)
    }

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
        notificationAdapter = NotificationAdapter(
//            requireContext(),
            emptyList()
        )
        b.tasksRecyclerView.setLayoutManager(LinearLayoutManager(context))
        b.tasksRecyclerView.setAdapter(notificationAdapter)

        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                taskViewModel.tasksForNotification.collect { tasks ->
                    notificationAdapter.updateTasks(tasks)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}