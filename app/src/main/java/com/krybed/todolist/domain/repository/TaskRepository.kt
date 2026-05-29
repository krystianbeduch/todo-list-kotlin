package com.krybed.todolist.domain.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.krybed.todolist.data.db.AppDatabase
import com.krybed.todolist.data.model.Task

class TaskRepository(ctx: Context) {

    private val taskDao = AppDatabase.getInstance(ctx).taskDao()

    fun getSortedTasks(): LiveData<List<Task>> =
        taskDao.getTasksOrderDescByCreatedDate()

    fun getById(id: Int): LiveData<Task?> =
        taskDao.getById(id)

    fun insert(task: Task) =
        taskDao.insert(task)

    fun update(task: Task) =
        taskDao.update(task)

    fun delete(task: Task) =
        taskDao.delete(task)

    fun deleteAll() =
        taskDao.deleteAll()

    fun changeStatus(taskId: Int, isDone: Boolean) =
        taskDao.changeStatus(taskId, isDone)
}