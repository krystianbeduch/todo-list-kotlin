package com.krybed.todolist.data.mapper

import com.krybed.todolist.data.model.AttachmentEntity
import com.krybed.todolist.domain.model.Attachment

class AttachmentMapper {
    fun toDomain(entity: AttachmentEntity): Attachment {
        return Attachment(
            id = entity.id,
            taskId = entity.taskId,
            filename = entity.filename,
            filePath = entity.filePath
        )
    }

    fun toEntity(domain: Attachment): AttachmentEntity {
        return AttachmentEntity(
            id = domain.id,
            taskId = domain.taskId,
            filename = domain.filename,
            filePath = domain.filePath
        )
    }
}