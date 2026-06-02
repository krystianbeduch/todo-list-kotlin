package com.krybed.todolist.domain.model

data class Attachment(
    val id: Int = 0,
    val taskId: Int,
    val filename: String,
    val filePath: String
)