package com.krybed.todolist.data.mapper

import com.krybed.todolist.data.model.AttachmentEntity
import com.krybed.todolist.data.model.TaskEntity
import com.krybed.todolist.data.model.TaskWithAttachments
import com.krybed.todolist.data.model.enums.NotificationType
import com.krybed.todolist.data.model.enums.Priority
import com.krybed.todolist.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class TaskMapperTest {

    @Test
    fun toDomain_shouldMapTaskEntityToTask() {
        val createdAt = LocalDateTime.of(2026, 6, 1, 10, 0)
        val deadline = LocalDateTime.of(2026, 6, 5, 12, 0)

        val entity = TaskEntity(
            id = 1,
            title = "Test task",
            deadline = deadline,
            isDone = true,
            priority = Priority.HIGH,
            createdAt = createdAt
        )

        val result = with(TaskMapper) { entity.toDomain() }

        assertEquals(1, result.id)
        assertEquals("Test task", result.title)
        assertEquals(deadline, result.deadline)
        assertEquals(true, result.isDone)
        assertEquals(Priority.HIGH, result.priority)
        assertEquals(createdAt, result.createdAt)
        assertTrue(result.attachments.isEmpty())
        assertEquals(NotificationType.NONE, result.notificationType)
    }

    @Test
    fun toEntity_shouldMapTaskToTaskEntity() {
        val createdAt = LocalDateTime.of(2026, 6, 1, 10, 0)
        val deadline = LocalDateTime.of(2026, 6, 5, 12, 0)

        val task = Task(
            id = 5,
            title = "Domain task",
            deadline = deadline,
            isDone = false,
            priority = Priority.LOW,
            createdAt = createdAt
        )

        val result = with(TaskMapper) { task.toEntity() }

        assertEquals(5, result.id)
        assertEquals("Domain task", result.title)
        assertEquals(deadline, result.deadline)
        assertEquals(false, result.isDone)
        assertEquals(Priority.LOW, result.priority)
        assertEquals(createdAt, result.createdAt)
    }

    @Test
    fun taskWithAttachmentsToDomain_shouldMapTaskAndAttachments() {
        val relation = TaskWithAttachments(
            task = TaskEntity(
                id = 1,
                title = "Task with attachments",
                deadline = LocalDateTime.of(2026, 6, 10, 12, 0),
                isDone = false,
                priority = Priority.MEDIUM,
                createdAt = LocalDateTime.of(2026, 6, 1, 8, 0)
            ),
            attachments = listOf(
                AttachmentEntity(
                    id = 1,
                    taskId = 1,
                    filename = "a.csv",
                    filePath = "/tmp/a.csv"
                ),
                AttachmentEntity(
                    id = 2,
                    taskId = 1,
                    filename = "b.json",
                    filePath = "/tmp/b.json"
                )
            )
        )

        val result = with(TaskMapper) { relation.toDomain() }

        assertEquals("Task with attachments", result.title)
        assertEquals(2, result.attachments.size)
        assertEquals("a.csv", result.attachments[0].filename)
        assertEquals("b.json", result.attachments[1].filename)
    }
}