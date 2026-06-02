package com.krybed.todolist.domain.repository

import android.content.Context
import com.krybed.todolist.data.db.AppDatabase
import com.krybed.todolist.data.model.AttachmentEntity
import com.krybed.todolist.domain.model.Attachment
import kotlinx.coroutines.flow.Flow

//class AttachmentRepository(ctx: Context) {
//
//    private val attachmentDao = AppDatabase.getInstance(ctx).attachmentDao()
//
//    fun getByTaskId(id: Int): List<AttachmentEntity> =
//        attachmentDao.getAttachmentsForTask(id)
//
//    fun insert(attachment: AttachmentEntity) =
//        attachmentDao.insert(attachment)
//
//    fun delete(attachment: AttachmentEntity) =
//        attachmentDao.delete(attachment)
//}

interface AttachmentRepository {
    suspend fun getByTaskId(taskId: Int): Flow<List<Attachment>>
    suspend fun insert(attachment: Attachment)
    suspend fun delete(attachment: Attachment)
}