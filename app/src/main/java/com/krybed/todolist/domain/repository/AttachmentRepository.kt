package com.krybed.todolist.domain.repository

import com.krybed.todolist.domain.model.Attachment
import kotlinx.coroutines.flow.Flow

interface AttachmentRepository {
    fun getByTaskId(taskId: Int): Flow<List<Attachment>>
    suspend fun insert(attachment: Attachment)
    suspend fun delete(attachment: Attachment)
}