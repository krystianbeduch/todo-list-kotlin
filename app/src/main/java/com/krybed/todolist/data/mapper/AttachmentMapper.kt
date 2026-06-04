package com.krybed.todolist.data.mapper

import com.krybed.todolist.data.model.AttachmentEntity
import com.krybed.todolist.domain.model.Attachment

object AttachmentMapper {
    fun AttachmentEntity.toDomain(): Attachment =
        Attachment(
            id = id,
            taskId = taskId,
            filename = filename,
            filePath = filePath
        )

    fun Attachment.toEntity(): AttachmentEntity =
        AttachmentEntity(
            id = id,
            taskId = taskId,
            filename = filename,
            filePath = filePath
        )
}