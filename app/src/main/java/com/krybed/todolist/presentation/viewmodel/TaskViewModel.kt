package com.krybed.todolist.presentation.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.compose.ui.text.style.TextDecoration.Companion.combine
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krybed.todolist.data.model.TaskEntity
import com.krybed.todolist.data.model.AttachmentEntity
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
import java.util.concurrent.Executors

//class TaskViewModel(application: Application) : AndroidViewModel(application) {
class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val attachmentRepository: AttachmentRepository
) : ViewModel() {

    private val _sortType = MutableStateFlow(SortType.CREATED_DATE)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    private val _tasksForNotification = MutableStateFlow<List<Task>>(emptyList())
    val tasksForNotification: StateFlow<List<Task>> = _tasksForNotification.asStateFlow()

    private val _hasInsertedDummy = MutableStateFlow(false)
    val hasInsertedDummy: StateFlow<Boolean> = _hasInsertedDummy.asStateFlow()

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

//    val tasks = MediatorLiveData<List<TaskEntity>?>()
//    val tasksForNotification = MutableLiveData<List<TaskEntity>>()
//    val hasInsertedDummy = MutableLiveData(false)
//    val notificationChecked = MutableLiveData(false)

    //    private val taskRepository = TaskRepository(application)
//    private val attachmentRepository = AttachmentRepository(application)
    private val executor = Executors.newSingleThreadExecutor()
    private var currentSource: LiveData<List<TaskEntity>>? = null
    private var currentSortType: SortType = SortType.CREATED_DATE

//    init {
//        loadTasksBySort(SortType.CREATED_DATE)
//    }

//    fun loadTasksBySort(sortType: SortType) {
//        currentSortType = sortType
//        val newSource = taskRepository.getSortedTasks()
//
//        currentSource?.let { tasks.removeSource(it) }
//        currentSource = newSource
//
//        tasks.addSource(newSource) { list ->
//            if (list == null) {
//                tasks.value = null
//                return@addSource
//            }
//
//            executor.execute {
//                list.forEach { task ->
//                    task.attachments = attachmentRepository.getByTaskId(task.id).toMutableList()
//                }
//
//                Handler(Looper.getMainLooper()).post {
//                    sortTasks(list.toMutableList(), currentSortType)
//                }
//            }
//        }
//    }

    fun loadTasksBySort(sortType: SortType) {
        _sortType.value = sortType
    }

    fun getTaskById(id: Int): Flow<Task?> =
        taskRepository.getById(id)

    suspend fun getAttachmentsByTaskId(taskId: Int): Flow<List<Attachment>> =
        attachmentRepository.getByTaskId(taskId)

    fun insert(task: Task) {
        viewModelScope.launch {
            taskRepository.insert(task)
        }
    }

//    fun insert(task: TaskEntity) {
//        executor.execute {
//            taskRepository.insert(task)
//        }
//    }

    fun update(task: Task) {
        viewModelScope.launch {
            taskRepository.update(task)
        }
    }

//    fun update(task: TaskEntity) {
//        executor.execute {
//            taskRepository.update(task)
//        }
//    }

    fun delete(task: Task) {
        viewModelScope.launch {
            taskRepository.delete(task)
        }
    }

//    fun delete(task: TaskEntity) {
//        executor.execute {
//            taskRepository.delete(task)
//        }
//    }

    fun changeStatus(task: Task) {
        viewModelScope.launch {
            taskRepository.changeStatus(task.id, !task.isDone)
        }
    }

    //    fun changeStatus(task: TaskEntity) {
//        executor.execute {
//            taskRepository.changeStatus(task.id, !task.isDone)
//            val currentTasks = tasks.value?.toMutableList()
//            sortTasks(currentTasks, currentSortType)
//        }
//    }
    fun addAttachmentToTask(attachment: Attachment) {
        viewModelScope.launch {
            attachmentRepository.insert(attachment)
        }
//        executor.execute {
//            attachmentRepository.insert(attachment)
//
//            Handler(Looper.getMainLooper()).post {
//                loadTasksBySort(currentSortType)
//            }
//        }
    }

//    fun addAttachmentToTask(attachment: AttachmentEntity) {
//        executor.execute {
//            attachmentRepository.insert(attachment)
//
//            Handler(Looper.getMainLooper()).post {
//                loadTasksBySort(currentSortType)
//            }
//        }
//    }

    fun deleteAttachment(attachment: Attachment) {
        viewModelScope.launch {
            attachmentRepository.delete(attachment)
        }
    }

//    fun deleteAttachment(attachment: AttachmentEntity) {
//        executor.execute {
//            attachmentRepository.delete(attachment)
//
//            Handler(Looper.getMainLooper()).post {
//                loadTasksBySort(currentSortType)
//            }
//        }
//    }

    fun updateTasksForNotification(tasks: List<Task>) {
        _tasksForNotification.value = tasks
    }

//    fun updateTasksForNotification(tasks: List<TaskEntity>) {
//        tasksForNotification.value = tasks
//    }

    fun markDummyInserted() {
        _hasInsertedDummy.value = true
    }

//    fun markDummyInserted() {
//        hasInsertedDummy.value = true
//    }

    fun markNotificationChecked() {
        _notificationChecked.value = true
    }


//    fun markNotificationChecked() {
//        notificationChecked.value = true
//    }

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



//    fun sortTasks(tasks: MutableList<TaskEntity>?, sortType: SortType) {
//        if (tasks == null) {
//            this.tasks.postValue(null)
//            return
//        }
//
//        val comparator = when (sortType) {
//            SortType.TITLE -> compareBy<TaskEntity> { it.title.lowercase() }
//            SortType.DEADLINE -> compareBy { it.deadline }
//            SortType.PRIORITY -> compareBy { it.priority }
//            SortType.STATUS -> compareBy { it.isDone }
//            else -> compareByDescending { it.createdAt }
//        }
//
//        tasks.sortWith(comparator)
//        currentSortType = sortType
//        this.tasks.postValue(tasks)
//    }

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

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }
}