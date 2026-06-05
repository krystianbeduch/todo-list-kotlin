package com.krybed.todolist

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.krybed.todolist.data.dao.AttachmentDao
import com.krybed.todolist.data.dao.TaskDao
import com.krybed.todolist.data.db.AppDatabase
import com.krybed.todolist.data.model.AttachmentEntity
import com.krybed.todolist.data.model.TaskEntity
import com.krybed.todolist.data.model.enums.Priority
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

@RunWith(AndroidJUnit4::class)
class TaskDaoInstrumentedTest {

    private lateinit var database: AppDatabase
    private lateinit var taskDao: TaskDao
    private lateinit var attachmentDao: AttachmentDao

    @Before
    fun createDb() {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            ctx,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        taskDao = database.taskDao()
        attachmentDao = database.attachmentDao()
    }

    @After
    fun closeDb() =
        database.close()

    @Test
    fun getTasksOrderDescByCreatedDate_shouldReturnTasksInDescendingOrder() = runTest {
        val older = testTaskEntity(
            title = "Older",
            createdAt = LocalDateTime.now().minusDays(1)
        )

        val newer = testTaskEntity(
            title = "Newer",
            createdAt = LocalDateTime.now()
        )

        taskDao.insertAll(listOf(older, newer))

        val result = taskDao.getTasksOrderDescByCreatedDate().first()

        assertEquals(2, result.size)
        assertEquals("Newer", result[0].task.title)
        assertEquals("Older", result[1].task.title)
    }

    @Test
    fun getById_shouldReturnsCorrectTask() = runTest {
        taskDao.insert(testTaskEntity())
        val insertedTask = taskDao.getAllOnce().first().task

        val result = taskDao.getById(insertedTask.id).first()

        assertNotNull(result)
        assertEquals("Test task", result?.task?.title)
    }

    @Test
    fun getById_shouldReturnsNllForInvalidId() = runTest {
        val result = taskDao.getById(999).first()

        assertNull(result)
    }

    @Test
    fun insertTaskAndGetAllOnce_shouldReturnsInsertedTasks() = runTest {
        val entity = testTaskEntity()

        taskDao.insert(entity)

        val allTasks = taskDao.getAllOnce()

        assertEquals(1, allTasks.size)
        assertEquals("Test task", allTasks.first().task.title)
    }

    @Test
    fun deleteTask_shouldRemovesTaskFromDatabase() = runTest {
        taskDao.insert(testTaskEntity())
        val insertedTask = taskDao.getAllOnce().first().task

        taskDao.delete(insertedTask)

        assertTrue(taskDao.getAllOnce().isEmpty())
    }

    @Test
    fun updateTask_shouldUpdatesTaskFields() = runTest {
        taskDao.insert(testTaskEntity(
            title = "Original",
            priority = Priority.LOW
        ))
        val insertedTask = taskDao.getAllOnce().first().task

        val updatedTask = insertedTask.copy(
            title = "Updated",
            priority = Priority.HIGH
        )

        taskDao.update(updatedTask)

        val result = taskDao.getAllOnce().first().task
        assertEquals("Updated", result.title)
        assertEquals(Priority.HIGH, result.priority)
    }

    @Test
    fun changeStatus_shouldUpdatesDoneFlag() = runTest {
        taskDao.insert(testTaskEntity(
            isDone = false
        ))
        val insertedTask = taskDao.getAllOnce().first().task

        taskDao.changeStatus(insertedTask.id, true)

        assertTrue(taskDao.getAllOnce().first().task.isDone)
    }

    @Test
    fun deleteAll_shouldRemovesAllTasks() = runTest {
        taskDao.insert(testTaskEntity(
            title = "Task 1"
        ))
        taskDao.insert(testTaskEntity(
            title = "Task 2"
        ))

        taskDao.deleteAll()

        assertTrue(taskDao.getAllOnce().isEmpty())
    }

    @Test
    fun getAllOnce_returnsTaskWithAttachments() = runTest {
        taskDao.insert(testTaskEntity(
            title = "Task with attachment"
        ))
        val insertedTask = taskDao.getAllOnce().first().task

        attachmentDao.insert(
            AttachmentEntity(
                taskId = insertedTask.id,
                filename = "file.csv",
                filePath = "/tmp/file.csv"
            )
        )

        val result = taskDao.getAllOnce().first()

        assertEquals("Task with attachment", result.task.title)
        assertEquals(1, result.attachments.size)
        assertEquals("file.csv", result.attachments.first().filename)
    }





    private fun testTaskEntity(
        title: String = "Test task",
        deadline: LocalDateTime = LocalDateTime.now().plusDays(3),
        isDone: Boolean = false,
        priority: Priority = Priority.LOW,
        createdAt: LocalDateTime = LocalDateTime.now()
    ): TaskEntity = TaskEntity(
        title = title,
        deadline = deadline,
        isDone = isDone,
        priority = priority,
        createdAt = createdAt
    )
}