package com.krybed.todolist.domain.repository

import com.krybed.todolist.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getSortedTasks(): Flow<List<Task>>
    fun getById(id: Int): Flow<Task?>
    suspend fun insert(task: Task)
    suspend fun update(task: Task)
    suspend fun delete(task: Task)
    suspend fun deleteAll()
    suspend fun changeStatus(taskId: Int, isDone: Boolean)

    // File
    suspend fun getAllOnce(): List<Task>
    suspend fun insertAll(tasks: List<Task>)
}