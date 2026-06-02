package com.krybed.todolist.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithAttachments(
    @Embedded
    val task: TaskEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "task_id"
    )
    val attachments: List<AttachmentEntity>
)
