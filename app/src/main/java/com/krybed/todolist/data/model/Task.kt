package com.krybed.todolist.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.krybed.todolist.data.model.enums.Attachment
import com.krybed.todolist.data.model.enums.NotificationType
import com.krybed.todolist.data.model.enums.Priority
import java.time.LocalDateTime

@Entity
data class Task (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,

    var title: String = "",

    var deadline: LocalDateTime = LocalDateTime.now().plusHours(24),

    @ColumnInfo(name = "is_done")
    var isDone: Boolean = false,

    @ColumnInfo(name = "priority")
    var priority: Priority = Priority.HIGH,

    @ColumnInfo(name = "created_at")
    var createdAt: LocalDateTime = LocalDateTime.now()
) {
    @Ignore
    var attachments: List<Attachment> = emptyList()

    @Ignore
    var notificationType: NotificationType = NotificationType.NONE

    companion object {
        const val FIELD_ID = "id"
        const val FIELD_TITLE = "title"
        const val FIELD_DEADLINE = "deadline"
        const val FIELD_PRIORITY = "priority"
        const val FIELD_IS_DONE = "isDone"
        const val FIELD_CREATED_AT = "createdAt"
    }
}