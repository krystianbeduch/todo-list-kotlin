package com.krybed.todolist.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.krybed.todolist.domain.repository.AttachmentRepository
import com.krybed.todolist.domain.repository.TaskRepository

class TaskViewModelFactory(
    private val taskRepository: TaskRepository,
    private val attachmentRepository: AttachmentRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(
                taskRepository,
                attachmentRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}