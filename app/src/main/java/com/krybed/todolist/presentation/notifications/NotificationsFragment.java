package com.krybed.todolist.presentation.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

//import com.example.todolist.databinding.FragmentNotificationsBinding;
//import com.example.todolist.presentation.notifications.adapter.NotificationAdapter;
//import com.example.todolist.presentation.viewmodel.TaskViewModel;

import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

//    private FragmentNotificationsBinding binding;
//    private NotificationAdapter notificationAdapter;
//
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
//        notificationAdapter = new NotificationAdapter(requireContext(), new ArrayList<>());
//        binding.tasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        binding.tasksRecyclerView.setAdapter(notificationAdapter);
//
//        TaskViewModel taskViewModel = new ViewModelProvider(requireActivity()).get(TaskViewModel.class);
//        taskViewModel.getTasksForNotification().observe(getViewLifecycleOwner(), tasks -> notificationAdapter.setTasks(tasks));
//
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
}