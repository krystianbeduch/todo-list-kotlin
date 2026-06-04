package com.krybed.todolist.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.krybed.todolist.data.model.enums.Priority
import java.time.LocalDateTime

@Entity
data class TaskEntity (

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String = "",

    val deadline: LocalDateTime = LocalDateTime.now().plusHours(24),

    @ColumnInfo(name = "is_done")
    val isDone: Boolean = false,

    @ColumnInfo(name = "priority")
    val priority: Priority = Priority.HIGH,

    @ColumnInfo(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
)