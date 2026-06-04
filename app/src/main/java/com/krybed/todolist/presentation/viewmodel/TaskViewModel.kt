package com.krybed.todolist.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krybed.todolist.data.model.enums.FileType
import com.krybed.todolist.data.model.enums.SortType
import com.krybed.todolist.domain.model.Attachment
import com.krybed.todolist.domain.model.Task
import com.krybed.todolist.domain.repository.AttachmentRepository
import com.krybed.todolist.domain.repository.TaskRepository
import com.krybed.todolist.util.file.FileService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val attachmentRepository: AttachmentRepository
) : ViewModel() {

    private val _sortType = MutableStateFlow(SortType.CREATED_DATE)

    private val _tasksForNotification = MutableStateFlow<List<Task>>(emptyList())
    val tasksForNotification: StateFlow<List<Task>> = _tasksForNotification.asStateFlow()

    private val _notificationChecked = MutableStateFlow(false)
    val notificationChecked: StateFlow<Boolean> = _notificationChecked.asStateFlow()

    val tasks: StateFlow<List<Task>> =
        combine(
            taskRepository.getSortedTasks(),
            _sortType
        ) { tasks, sortType ->
            sortTasks(tasks, sortType)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun loadTasksBySort(sortType: SortType) {
        _sortType.value = sortType
    }

    suspend fun getAllOnce(): List<Task> =
        taskRepository.getAllOnce()

    fun getTaskById(id: Int): Flow<Task?> =
        taskRepository.getById(id)

    fun insert(task: Task) {
        viewModelScope.launch {
            taskRepository.insert(task)
        }
    }

    fun update(task: Task) {
        viewModelScope.launch {
            taskRepository.update(task)
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            taskRepository.delete(task)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            taskRepository.deleteAll()
        }
    }

    fun changeStatus(task: Task) {
        viewModelScope.launch {
            taskRepository.changeStatus(task.id, !task.isDone)
        }
    }

    fun addAttachmentToTask(attachment: Attachment) {
        viewModelScope.launch {
            attachmentRepository.insert(attachment)
        }
    }

    fun deleteAttachment(attachment: Attachment) {
        viewModelScope.launch {
            attachmentRepository.delete(attachment)
        }
    }

    fun updateTasksForNotification(tasks: List<Task>) {
        _tasksForNotification.value = tasks
    }

    fun markNotificationChecked() {
        _notificationChecked.value = true
    }

    fun importTasksFromFile(ctx: Context, uri: Uri, fileType: FileType) {
        viewModelScope.launch {
            when (fileType) {
                FileType.CSV -> FileService.importTasksFromCsv(ctx, uri, taskRepository)
                FileType.JSON -> FileService.importTasksFromJson(ctx, uri, taskRepository)
                FileType.XML -> FileService.importTasksFromXml(ctx, uri, taskRepository)
            }
        }
    }

    fun exportTasksToFile(ctx: Context, fileType: FileType) {
        viewModelScope.launch {
            when (fileType) {
                FileType.CSV -> FileService.exportTasksToCsv(ctx, taskRepository)
                FileType.JSON -> FileService.exportTasksToJson(ctx, taskRepository)
                FileType.XML -> FileService.exportTasksToXml(ctx, taskRepository)
            }
        }
    }

    private fun sortTasks(tasks: List<Task>, sortType: SortType): List<Task> {
        val comparator = when (sortType) {
            SortType.TITLE -> compareBy<Task> { it.title.lowercase() }
            SortType.DEADLINE -> compareBy { it.deadline }
            SortType.PRIORITY -> compareBy { it.priority }
            SortType.STATUS -> compareBy { it.isDone }
            SortType.CREATED_DATE -> compareByDescending { it.createdAt }
        }
        return tasks.sortedWith(comparator)
    }
}