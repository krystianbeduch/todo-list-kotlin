package com.krybed.todolist.data.repository

import com.krybed.todolist.data.dao.TaskDao
import com.krybed.todolist.data.model.AttachmentEntity
import com.krybed.todolist.data.model.TaskEntity
import com.krybed.todolist.data.model.TaskWithAttachments
import com.krybed.todolist.data.model.enums.NotificationType
import com.krybed.todolist.data.model.enums.Priority
import com.krybed.todolist.domain.model.Task
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.time.LocalDateTime

class TaskRepositoryImplTest {

    private lateinit var taskDao: TaskDao
    private lateinit var repository: TaskRepositoryImpl

    @Before
    fun setUp() {
        taskDao = mock()
        repository = TaskRepositoryImpl(taskDao)
    }

    @Test
    fun getSortedTasks_shouldMapRelationsToDomain() = runTest {
        whenever(taskDao.getTasksOrderDescByCreatedDate()).thenReturn(
            flowOf(
                listOf(
                    TaskWithAttachments(
                        task = testTaskEntity(title = "Task 1"),
                        attachments = listOf(
                            AttachmentEntity(
                                id = 1,
                                taskId = 0,
                                filename = "file.csv",
                                filePath = "/tmp/file.csv"
                            )
                        )
                    )
                )
            )
        )

        val result = repository.getSortedTasks().first()

        assertEquals(1, result.size)
        assertEquals("Task 1", result.first().title)
        assertEquals(1, result.first().attachments.size)
    }

    @Test
    fun getById_shouldMapRelationToDomain() = runTest {
        whenever(taskDao.getById(1)).thenReturn(
            flowOf(
                TaskWithAttachments(
                    task = testTaskEntity(id = 1, title = "Task by id"),
                    attachments = emptyList()
                )
            )
        )

        val result = repository.getById(1).first()

        assertEquals("Task by id", result?.title)
    }

    @Test
    fun insert_shouldMapDomainToEntityAndCallDao() = runTest {
        repository.insert(testTask(
            id = 7,
            title = "Insert me"
        ))

        val captor = argumentCaptor<TaskEntity>()
        verify(taskDao).insert(captor.capture())
        assertEquals(7, captor.firstValue.id)
        assertEquals("Insert me", captor.firstValue.title)
    }

    @Test
    fun update_shouldMapDomainToEntityAndCallDao() = runTest {
        repository.update(testTask(
            id = 8,
            title = "Update me"
        ))

        val captor = argumentCaptor<TaskEntity>()
        verify(taskDao).update(captor.capture())
        assertEquals(8, captor.firstValue.id)
        assertEquals("Update me", captor.firstValue.title)
    }

    @Test
    fun delete_shouldMapDomainToEntityAndCallDao() = runTest {
        repository.delete(testTask(
            id = 9, title = "Delete me"
        ))

        val captor = argumentCaptor<TaskEntity>()
        verify(taskDao).delete(captor.capture())
        assertEquals(9, captor.firstValue.id)
        assertEquals("Delete me", captor.firstValue.title)
    }

    @Test
    fun deleteAll_shouldCallDao() = runTest {
        repository.deleteAll()

        verify(taskDao).deleteAll()
    }

    @Test
    fun changeStatus_shouldCallDaoWithArguments() = runTest {
        repository.changeStatus(11, true)

        verify(taskDao).changeStatus(11, true)
    }

    @Test
    fun getAllOnce_shouldMapRelationsToDomain() = runTest {
        whenever(taskDao.getAllOnce()).thenReturn(
            listOf(
                TaskWithAttachments(
                    task = testTaskEntity(title = "Mapped task"),
                    attachments = emptyList()
                )
            )
        )

        val result = repository.getAllOnce()

        assertEquals(1, result.size)
        assertEquals("Mapped task", result.first().title)
    }

    @Test
    fun insertAll_shouldMapDomainListToEntityListAndCallDao() = runTest {
        val tasks = listOf(
            testTask(id = 1, title = "A"),
            testTask(id = 2, title = "B")
        )

        repository.insertAll(tasks)

        val captor = argumentCaptor<List<TaskEntity>>()
        verify(taskDao).insertAll(captor.capture())
        assertEquals(2, captor.firstValue.size)
        assertEquals("A", captor.firstValue[0].title)
        assertEquals("B", captor.firstValue[1].title)
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

    private fun testTaskEntity(
        id: Int = 0,
        title: String = "Task",
        deadline: LocalDateTime = LocalDateTime.now().plusDays(1),
        isDone: Boolean = false,
        priority: Priority = Priority.LOW,
        createdAt: LocalDateTime = LocalDateTime.now()
    ) = TaskEntity(
        id = id,
        title = title,
        deadline = deadline,
        isDone = isDone,
        priority = priority,
        createdAt = createdAt
    )
}