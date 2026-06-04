package com.krybed.todolist.domain.model

import com.krybed.todolist.data.model.enums.NotificationType
import com.krybed.todolist.data.model.enums.Priority
import java.time.LocalDateTime

data class Task(
    val id: Int = 0,
    val title: String = "",
    val deadline: LocalDateTime = LocalDateTime.now().plusHours(24),
    val isDone: Boolean = false,
    val priority: Priority = Priority.HIGH,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val attachments: List<Attachment> = emptyList(),
    val notificationType: NotificationType = NotificationType.NONE
) {
    companion object {
        fun create(
            title: String,
            deadline: LocalDateTime,
            isDone: Boolean,
            priority: Priority,
            createdAt: LocalDateTime = LocalDateTime.now()
        ): Task = Task(
            title = title,
            deadline = deadline,
            isDone = isDone,
            priority = priority,
            createdAt = createdAt
        )
    }
}