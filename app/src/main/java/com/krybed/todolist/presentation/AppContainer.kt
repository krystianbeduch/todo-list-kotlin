package com.krybed.todolist.presentation

import android.content.Context
import com.krybed.todolist.data.db.AppDatabase
import com.krybed.todolist.data.repository.AttachmentRepositoryImpl
import com.krybed.todolist.data.repository.TaskRepositoryImpl
import com.krybed.todolist.presentation.viewmodel.TaskViewModelFactory

object AppContainer {

    fun provideTaskViewModelFactory(ctx: Context): TaskViewModelFactory {
        val appContext = ctx.applicationContext
        val db = AppDatabase.getInstance(appContext)

        val taskRepository = TaskRepositoryImpl(
            db.taskDao()
        )

        val attachmentRepository = AttachmentRepositoryImpl(
            db.attachmentDao()
        )

        return TaskViewModelFactory(
            taskRepository,
            attachmentRepository
        )
    }
}