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
        const val FIELD_ID = "id"
        const val FIELD_TITLE = "title"
        const val FIELD_DEADLINE = "deadline"
        const val FIELD_PRIORITY = "priority"
        const val FIELD_IS_DONE = "isDone"
        const val FIELD_CREATED_AT = "createdAt"

        fun create(
            title: String,
            deadline: LocalDateTime,
            isDone: Boolean,
            priority: Priority,
            createdAt: LocalDateTime = LocalDateTime.now()
        ): Task {
            return Task(
                title = title,
                deadline = deadline,
                isDone = isDone,
                priority = priority,
                createdAt = createdAt
            )
        }
    }
}