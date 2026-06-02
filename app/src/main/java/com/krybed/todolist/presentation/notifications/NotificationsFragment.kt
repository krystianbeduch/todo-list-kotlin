package com.krybed.todolist.presentation.notifications;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.krybed.todolist.databinding.FragmentNotificationsBinding
import com.krybed.todolist.presentation.AppContainer
import com.krybed.todolist.presentation.applyRecyclerViewInsets
import com.krybed.todolist.presentation.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class NotificationsFragment : Fragment() {

    private var binding: FragmentNotificationsBinding? = null
    private val b get() = binding!!

    private val taskViewModel: TaskViewModel by activityViewModels {
        AppContainer.provideTaskViewModelFactory(requireContext().applicationContext)
    }

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

//        val taskViewModel: TaskViewModel =
//            ViewModelProvider(requireActivity())[TaskViewModel::class.java]

//        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                taskViewModel.tasksForNotification.collect { tasks ->
//                    notificationAdapter.updateTasks(tasks)
//                }
//            }
//        }

//        taskViewModel.tasksForNotification.observe(viewLifecycleOwner) { tasks ->
//            notificationAdapter.updateTasks(tasks)
//            notificationAdapter.setTasks(tasks
//        }
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        b.tasksRecyclerView.applyRecyclerViewInsets()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                taskViewModel.tasksForNotification.collect { tasks ->
                    notificationAdapter.updateTasks(tasks)
                }
            }
        }

//        ViewCompat.setOnApplyWindowInsetsListener(b.tasksRecyclerView) { recyclerView, insets ->
//            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            recyclerView.setPadding(
//                recyclerView.paddingLeft,
//                bars.top,
//                recyclerView.paddingRight,
//                bars.bottom + dpToPx(72)
//            )
//            insets
//        }
//        ViewCompat.requestApplyInsets(b.tasksRecyclerView)
    }
    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}