package com.krybed.todolist.util.file.json

import com.krybed.todolist.data.model.enums.Priority
import com.krybed.todolist.domain.model.Task
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class TaskJsonDto(

    @SerialName("id")
    val id: Int = 0,

    @SerialName("title")
    val title: String = "",

    @Serializable(with = LocalDateTimeAsStringSerializer::class)
    @SerialName("deadline")
    val deadline: LocalDateTime,

    @SerialName("priority")
    val priority: String = Priority.HIGH.name,

    @SerialName("isDone")
    val isDone: Boolean = false,

    @Serializable(with = LocalDateTimeAsStringSerializer::class)
    @SerialName("createdAt")
    val createdAt: LocalDateTime
) {
}
//    fun Task.toJsonDto(): TaskJsonDto =
//        TaskJsonDto(
//            id = id,
//            title = title,
//            deadline = deadline,
//            priority = priority.name,
//            isDone = isDone,
//            createdAt = createdAt
//        )
//
//    fun TaskJsonDto.toDomain(): Task =
//        Task(
//            id = id,
//            title = title,
//            deadline = deadline,
//            isDone = isDone,
//            priority = Priority.valueOf(priority),
//            createdAt = createdAt,
//            attachments = emptyList()
//        )
