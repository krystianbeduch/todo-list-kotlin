package com.krybed.todolist.util.file.json

import com.krybed.todolist.data.model.enums.Priority
import com.krybed.todolist.domain.model.Task

object TaskJsonMapper {
    fun Task.toJsonDto(): TaskJsonDto =
        TaskJsonDto(
            id = id,
            title = title,
            deadline = deadline,
            priority = priority.name,
            isDone = isDone,
            createdAt = createdAt
        )

    fun TaskJsonDto.toDomain(): Task =
        Task(
            id = id,
            title = title,
            deadline = deadline,
            isDone = isDone,
            priority = Priority.valueOf(priority),
            createdAt = createdAt,
            attachments = emptyList()
        )
}
