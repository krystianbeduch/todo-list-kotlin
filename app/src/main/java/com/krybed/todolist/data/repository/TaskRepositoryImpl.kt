package com.krybed.todolist.data.repository

import com.krybed.todolist.data.dao.TaskDao
import com.krybed.todolist.data.mapper.TaskMapper.toDomain
import com.krybed.todolist.data.mapper.TaskMapper.toEntity
import com.krybed.todolist.domain.model.Task
import com.krybed.todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getSortedTasks(): Flow<List<Task>> =
        taskDao.getTasksOrderDescByCreatedDate().map { list ->
                list.map { it.toDomain() }
        }

    override fun getById(id: Int): Flow<Task?> =
        taskDao.getById(id).map { it?.toDomain() }

    override suspend fun insert(task: Task) =
        taskDao.insert(task.toEntity())

    override suspend fun update(task: Task) =
        taskDao.update(task.toEntity())

    override suspend fun delete(task: Task) =
        taskDao.delete(task.toEntity())

    override suspend fun deleteAll() =
        taskDao.deleteAll()

    override suspend fun changeStatus(taskId: Int, isDone: Boolean) =
        taskDao.changeStatus(taskId, isDone)

    override suspend fun getAllOnce(): List<Task> =
        taskDao.getAllOnce().map { relation ->
            relation.toDomain()
        }

    override suspend fun insertAll(tasks: List<Task>) =
        taskDao.insertAll(tasks.map( { it.toEntity() }))
}