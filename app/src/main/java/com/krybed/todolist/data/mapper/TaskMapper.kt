package com.krybed.todolist.data.mapper

import com.krybed.todolist.data.model.TaskEntity
import com.krybed.todolist.data.model.enums.NotificationType
import com.krybed.todolist.domain.model.Task

class TaskMapper {
    fun toDomain(entity: TaskEntity): Task {
        return Task(
            id = entity.id,
            title = entity.title,
            deadline = entity.deadline,
            isDone = entity.isDone,
            priority = entity.priority,
            createdAt = entity.createdAt,
            attachments = emptyList(),
            notificationType = NotificationType.NONE
        )
    }

    fun toEntity(domain: Task): TaskEntity {
        return TaskEntity(
            id = domain.id,
            title = domain.title,
            deadline = domain.deadline,
            isDone = domain.isDone,
            priority = domain.priority,
            createdAt = domain.createdAt
        )
    }
}