package com.krybed.todolist.data.repository

import com.krybed.todolist.data.dao.AttachmentDao
import com.krybed.todolist.data.dao.TaskDao
import com.krybed.todolist.data.mapper.AttachmentMapper
import com.krybed.todolist.data.mapper.TaskMapper
import com.krybed.todolist.data.model.TaskEntity
import com.krybed.todolist.data.model.TaskWithAttachments
import com.krybed.todolist.domain.model.Task
import com.krybed.todolist.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
//    ctx: Context
    private val taskDao: TaskDao,
    private val taskMapper: TaskMapper,
    private val attachmentMapper: AttachmentMapper
) : TaskRepository {

//    private val taskDao = AppDatabase.getInstance(ctx).taskDao()
//    private val taskMapper: TaskMapper = Mappers.getMapper(TaskMapper::class.java)

    override fun getSortedTasks(): Flow<List<Task>> =
        taskDao.getTasksOrderDescByCreatedDate()
            .map { relations ->
                relations.map { it.toDomain() }
            }

//    fun getSortedTasks(): LiveData<List<TaskEntity>> =
//        taskDao.getTasksOrderDescByCreatedDate()

    override fun getById(id: Int): Flow<Task?> =
        taskDao.getById(id)
            .map { relation ->
                relation?.toDomain()
            }

//    fun getById(id: Int): LiveData<TaskEntity?> =
//        taskDao.getById(id)

    override suspend fun insert(task: Task) =
        taskDao.insert(taskMapper.toEntity(task))

//    fun insert(task: TaskEntity) =
//        taskDao.insert(task)

    override suspend fun update(task: Task) =
        taskDao.update(taskMapper.toEntity(task))

//    fun update(task: TaskEntity) =
//        taskDao.update(task)

    override suspend fun delete(task: Task) =
        taskDao.delete(taskMapper.toEntity(task))

//    fun delete(task: TaskEntity) =
//        taskDao.delete(task)

    override suspend fun deleteAll() =
        taskDao.deleteAll()

//    fun deleteAll() =
//        taskDao.deleteAll()

    override suspend fun changeStatus(taskId: Int, isDone: Boolean) =
        taskDao.changeStatus(taskId, isDone)

    override suspend fun getAll(): List<Task> =
        taskDao.getAll().map { relation ->
            relation.toDomain()
        }


    override suspend fun insertAll(tasks: List<Task>) =
        taskDao.insertAll(tasks.map(taskMapper::toEntity))

//    fun changeStatus(taskId: Int, isDone: Boolean) =
//        taskDao.changeStatus(taskId, isDone)

    private fun TaskWithAttachments.toDomain(): Task =
        taskMapper.toDomain(task).copy(
            attachments = attachments.map(attachmentMapper::toDomain)
        )
}