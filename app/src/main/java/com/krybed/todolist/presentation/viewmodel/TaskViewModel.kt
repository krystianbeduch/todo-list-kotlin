package com.krybed.todolist.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.krybed.todolist.data.model.Task
import com.krybed.todolist.data.model.enums.Attachment
import com.krybed.todolist.data.model.enums.FileType
import com.krybed.todolist.data.model.enums.SortType
import com.krybed.todolist.domain.repository.AttachmentRepository
import com.krybed.todolist.domain.repository.TaskRepository
import com.krybed.todolist.util.file.FileService
import java.net.URI
import java.util.concurrent.Executors

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    val tasks = MediatorLiveData<List<Task>?>()
    val tasksForNotification = MutableLiveData<List<Task>>()
    val hasInsertedDummy = MutableLiveData(false)
    val notificationChecked = MutableLiveData(false)

    private val taskRepository = TaskRepository(application)
    private val attachmentRepository = AttachmentRepository(application)
    private val executor = Executors.newSingleThreadExecutor()
    private var currentSource: LiveData<List<Task>>? = null
    private var currentSortType: SortType = SortType.CREATED_DATE

    init {
        loadTasksBySort(SortType.CREATED_DATE)
    }

    fun loadTasksBySort(sortType: SortType) {
        currentSortType = sortType
        val newSource = taskRepository.getSortedTasks()

        currentSource?.let { tasks.removeSource(it) }
        currentSource = newSource

        tasks.addSource(newSource) { list ->
            if (list == null) {
                tasks.value = null
                return@addSource
            }

            executor.execute {
                list.forEach { task ->
                    task.attachments = attachmentRepository.getByTaskId(task.id)
                }
                sortTasks(list.toMutableList(), currentSortType)
            }
        }
    }

    fun insert(task: Task) {
        executor.execute {
            taskRepository.insert(task)
        }
    }

    fun update(task: Task) {
        executor.execute {
            taskRepository.update(task)
        }
    }

    fun delete(task: Task) {
        executor.execute {
            taskRepository.delete(task)
        }
    }

    fun changeStatus(task: Task) {
        executor.execute {
            taskRepository.changeStatus(task.id, !task.isDone)
            val currentTasks = tasks.value?.toMutableList()
            sortTasks(currentTasks, currentSortType)
        }
    }

    fun sortTasks(tasks: MutableList<Task>?, sortType: SortType) {
        if (tasks == null) {
            this.tasks.postValue(null)
            return
        }

        val comparator = when (sortType) {
            SortType.TITLE -> compareBy<Task> { it.title.lowercase() }
            SortType.DEADLINE -> compareBy<Task> { it.deadline }
            SortType.PRIORITY -> compareBy<Task> { it.priority }
            SortType.STATUS -> compareBy<Task> { it.isDone }
            else -> compareByDescending { it.createdAt }
        }

        tasks.sortWith(comparator)
        currentSortType = sortType
        this.tasks.postValue(tasks)
    }

    fun addAttachmentToTask(attachment: Attachment) {
        executor.execute {
            attachmentRepository.insert(attachment)
            loadTasksBySort(currentSortType)
        }
    }

    fun deleteAttachment(attachment: Attachment) {
        executor.execute {
            attachmentRepository.delete(attachment)
            loadTasksBySort(currentSortType)
        }
    }

    fun updateTasksForNotification(tasks: List<Task>) {
        tasksForNotification.value = tasks
    }

    fun markDummyInserted() {
        hasInsertedDummy.value = true
    }

    fun markNotificationChecked() {
        notificationChecked.value = true
    }

    fun importTasksFromFile(ctx: Context, uri: Uri, fileType: FileType) =
        when (fileType) {
            FileType.CSV -> FileService.importTasksFromCsv(ctx, uri)
            FileType.JSON -> FileService.importTasksFromJson(ctx, uri)
            FileType.XML -> FileService.importTasksFromXml(ctx, uri)
        }

    fun exportTasksToFile(ctx: Context, fileType: FileType) =
        when (fileType) {
            FileType.CSV -> FileService.exportTasksToCsv(ctx)
            FileType.JSON -> FileService.exportTasksToJson(ctx)
            FileType.XML -> FileService.exportTasksToXml(ctx)
        }

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }
}