package com.krybed.todolist.data.repository

import com.krybed.todolist.data.dao.AttachmentDao
import com.krybed.todolist.data.mapper.AttachmentMapper.toDomain
import com.krybed.todolist.data.mapper.AttachmentMapper.toEntity
import com.krybed.todolist.domain.model.Attachment
import com.krybed.todolist.domain.repository.AttachmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AttachmentRepositoryImpl(
    private val attachmentDao: AttachmentDao
) : AttachmentRepository {

    override fun getByTaskId(taskId: Int): Flow<List<Attachment>> =
        attachmentDao.getByTaskId(taskId)
            .map{ entities ->
                entities.map { it.toDomain() }
            }

    override suspend fun insert(attachment: Attachment) {
        attachmentDao.insert(
            attachment.toEntity()
        )
    }

    override suspend fun delete(attachment: Attachment) {
        attachmentDao.delete(
            attachment.toEntity()
        )
    }
}