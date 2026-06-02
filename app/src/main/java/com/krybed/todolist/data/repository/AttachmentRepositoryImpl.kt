package com.krybed.todolist.data.repository

import com.krybed.todolist.data.dao.AttachmentDao
import com.krybed.todolist.data.mapper.AttachmentMapper
import com.krybed.todolist.domain.model.Attachment
import com.krybed.todolist.domain.repository.AttachmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AttachmentRepositoryImpl(
    private val attachmentDao: AttachmentDao,
    private val attachmentMapper: AttachmentMapper
) : AttachmentRepository {

    override suspend fun getByTaskId(taskId: Int): Flow<List<Attachment>> =
        attachmentDao.getByTaskId(taskId)
            .map{
                entities -> entities.map(attachmentMapper::toDomain)
            }

    override suspend fun insert(attachment: Attachment) {
        attachmentDao.insert(
            attachmentMapper.toEntity(attachment)
        )
    }

    override suspend fun delete(attachment: Attachment) {
        attachmentDao.delete(
            attachmentMapper.toEntity(attachment)
        )
    }
}