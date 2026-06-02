package com.krybed.todolist.presentation

import android.content.Context
import com.krybed.todolist.data.db.AppDatabase
import com.krybed.todolist.data.mapper.AttachmentMapper
import com.krybed.todolist.data.mapper.TaskMapper
import com.krybed.todolist.data.repository.AttachmentRepositoryImpl
import com.krybed.todolist.data.repository.TaskRepositoryImpl
import com.krybed.todolist.presentation.viewmodel.TaskViewModelFactory

object AppContainer {

    fun provideTaskViewModelFactory(ctx: Context): TaskViewModelFactory {
        val appContext = ctx.applicationContext
        val database = AppDatabase.getInstance(appContext)
        val attachmentMapper = AttachmentMapper()

        val taskRepository = TaskRepositoryImpl(
            database.taskDao(),
            TaskMapper(),
            attachmentMapper
        )

        val attachmentRepository = AttachmentRepositoryImpl(
            database.attachmentDao(),
            attachmentMapper
        )

        return TaskViewModelFactory(
            taskRepository,
            attachmentRepository
        )
    }
}