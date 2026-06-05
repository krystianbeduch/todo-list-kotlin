package com.krybed.todolist.presentation.viewmodel

import com.krybed.todolist.data.model.enums.NotificationType
import com.krybed.todolist.data.model.enums.Priority
import com.krybed.todolist.data.model.enums.SortType
import com.krybed.todolist.domain.model.Attachment
import com.krybed.todolist.domain.model.Task
import com.krybed.todolist.domain.repository.AttachmentRepository
import com.krybed.todolist.domain.repository.TaskRepository
import com.krybed.todolist.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var taskRepository: TaskRepository
    private lateinit var attachmentRepository: AttachmentRepository
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setUp() {
        taskRepository = mock()
        attachmentRepository = mock()

        whenever(taskRepository.getSortedTasks())
            .thenReturn(flowOf(emptyList()))

        viewModel = TaskViewModel(taskRepository, attachmentRepository)
    }

    @Test
    fun tasksForNotification_shouldBeEmptyInitially() {
        assertTrue(viewModel.tasksForNotification.value.isEmpty())
    }

    @Test
    fun notificationChecked_shouldBeFalseInitially() {
        assertFalse(viewModel.notificationChecked.value)
    }

    @Test
    fun updateTasksForNotification_shouldUpdateState() {
        val tasks = listOf(testTask(title = "Task 1"))

        viewModel.updateTasksForNotification(tasks)

        assertEquals(tasks, viewModel.tasksForNotification.value)
    }

    @Test
    fun markNotificationChecked_shouldSetTrue() {
        viewModel.markNotificationChecked()

        assertTrue(viewModel.notificationChecked.value)
    }

    @Test
    fun getAllOnce_shouldReturnTasksFromRepository() = runTest {
        val tasks = listOf(testTask(title = "A"))
        whenever(taskRepository.getAllOnce())
            .thenReturn(tasks)

        val result = viewModel.getAllOnce()

        assertEquals(tasks, result)
    }

    @Test
    fun getTaskById_shouldReturnTaskFromRepository() = runTest {
        val task = testTask(id = 1, title = "Find me")
        whenever(taskRepository.getById(1))
            .thenReturn(flowOf(task))

        val result = viewModel.getTaskById(1)

        assertEquals("Find me", result.first()?.title)
    }

    @Test
    fun insert_shouldCallRepositoryInsert() = runTest {
        val task = testTask()

        viewModel.insert(task)
        advanceUntilIdle()

        verify(taskRepository).insert(task)
    }

    @Test
    fun update_shouldCallRepositoryUpdate() = runTest {
        val task = testTask()

        viewModel.update(task)
        advanceUntilIdle()

        verify(taskRepository).update(task)
    }

    @Test
    fun delete_shouldCallRepositoryDelete() = runTest {
        val task = testTask()

        viewModel.delete(task)
        advanceUntilIdle()

        verify(taskRepository).delete(task)
    }

    @Test
    fun deleteAll_shouldCallRepositoryDeleteAll() = runTest {
        viewModel.deleteAll()
        advanceUntilIdle()

        verify(taskRepository).deleteAll()
    }

    @Test
    fun changeStatus_shouldCallRepositoryWithToggledFlag() = runTest {
        val task = testTask(id = 1, isDone = false)

        viewModel.changeStatus(task)
        advanceUntilIdle()

        verify(taskRepository).changeStatus(1, true)
    }

    @Test
    fun addAttachmentToTask_shouldCallAttachmentRepositoryInsert() = runTest {
        val attachment = Attachment(
            id = 1,
            taskId = 1,
            filename = "a.csv",
            filePath = "/tmp/a.csv"
        )

        viewModel.addAttachmentToTask(attachment)
        advanceUntilIdle()

        verify(attachmentRepository).insert(attachment)
    }

    @Test
    fun deleteAttachment_shouldCallAttachmentRepositoryDelete() = runTest {
        val attachment = Attachment(
            id = 1,
            taskId = 1,
            filename = "a.csv",
            filePath = "/tmp/a.csv"
        )

        viewModel.deleteAttachment(attachment)
        advanceUntilIdle()

        verify(attachmentRepository).delete(attachment)
    }

    @Test
    fun loadTasksBySort_shouldSortTasksByTitle() = runTest {
        val source = MutableStateFlow(
            listOf(
                testTask(title = "bbb"),
                testTask(title = "aaa")
            )
        )
        whenever(taskRepository.getSortedTasks()).thenReturn(source)

        viewModel = TaskViewModel(taskRepository, attachmentRepository)

        val collectJob = launch { viewModel.tasks.collect {} }
        advanceUntilIdle()

        viewModel.loadTasksBySort(SortType.TITLE)
        advanceUntilIdle()

        assertEquals(
            listOf("aaa", "bbb"),
            viewModel.tasks.value.map { it.title }
        )
        collectJob.cancel()
    }

    @Test
    fun loadTasksBySort_shouldSortTasksByCreatedDateDesc() = runTest {
        val source = MutableStateFlow(
            listOf(
                testTask(
                    title = "Older",
                    createdAt = LocalDateTime.of(2026, 6, 1, 10, 0)),
                testTask(
                    title = "Newer",
                    createdAt = LocalDateTime.of(2026, 6, 2, 10, 0))
            )
        )
        whenever(taskRepository.getSortedTasks())
            .thenReturn(source)

        viewModel = TaskViewModel(taskRepository, attachmentRepository)

        val collectJob = launch { viewModel.tasks.collect {} }
        advanceUntilIdle()

        viewModel.loadTasksBySort(SortType.CREATED_DATE)
        advanceUntilIdle()

        assertEquals(
            listOf("Newer", "Older"),
            viewModel.tasks.value.map { it.title }
        )

        collectJob.cancel()
    }

    private fun testTask(
        id: Int = 0,
        title: String = "Task",
        deadline: LocalDateTime = LocalDateTime.now().plusDays(1),
        isDone: Boolean = false,
        priority: Priority = Priority.LOW,
        createdAt: LocalDateTime = LocalDateTime.now()
    ) = Task(
        id = id,
        title = title,
        deadline = deadline,
        isDone = isDone,
        priority = priority,
        createdAt = createdAt,
        attachments = emptyList(),
        notificationType = NotificationType.NONE
    )
}