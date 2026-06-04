package com.krybed.todolist.data.mapper

import com.krybed.todolist.data.mapper.AttachmentMapper.toDomain
import com.krybed.todolist.data.model.TaskEntity
import com.krybed.todolist.data.model.TaskWithAttachments
import com.krybed.todolist.data.model.enums.NotificationType
import com.krybed.todolist.domain.model.Task

object TaskMapper {
    fun TaskEntity.toDomain(): Task =
        Task(
            id = id,
            title = title,
            deadline = deadline,
            isDone = isDone,
            priority = priority,
            createdAt = createdAt,
            attachments = emptyList(),
            notificationType = NotificationType.NONE
        )

    fun Task.toEntity(): TaskEntity =
        TaskEntity(
            id = id,
            title = title,
            deadline = deadline,
            isDone = isDone,
            priority = priority,
            createdAt = createdAt
        )

    fun TaskWithAttachments.toDomain(): Task =
        task.toDomain().copy(
            attachments = attachments.map { it.toDomain() }
        )
}