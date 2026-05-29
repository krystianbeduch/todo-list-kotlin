package com.krybed.todolist.domain.repository

import android.content.Context
import com.krybed.todolist.data.db.AppDatabase
import com.krybed.todolist.data.model.enums.Attachment

class AttachmentRepository(ctx: Context) {

    private val attachmentDao = AppDatabase.getInstance(ctx).attachmentDao()

    fun getByTaskId(id: Int): List<Attachment> =
        attachmentDao.getAttachmentsForTask(id)

    fun insert(attachment: Attachment) =
        attachmentDao.insert(attachment)

    fun delete(attachment: Attachment) =
        attachmentDao.delete(attachment)
}