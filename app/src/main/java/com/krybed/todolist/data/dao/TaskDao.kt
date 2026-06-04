package com.krybed.todolist.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.krybed.todolist.data.model.TaskEntity
import com.krybed.todolist.data.model.TaskWithAttachments
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Transaction
    @Query("SELECT * FROM TaskEntity ORDER BY created_at DESC")
    fun getTasksOrderDescByCreatedDate(): Flow<List<TaskWithAttachments>>

    @Transaction
    @Query("SELECT * FROM TaskEntity WHERE id = :id")
    fun getById(id: Int): Flow<TaskWithAttachments?>

    @Insert
    suspend fun insert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM TaskEntity")
    suspend fun deleteAll()

    @Query("UPDATE TaskEntity SET is_done = :isDone WHERE id = :taskId")
    suspend fun changeStatus(taskId: Int, isDone: Boolean)


    // File
    @Transaction
    @Query("SELECT * FROM TaskEntity")
    suspend fun getAllOnce(): List<TaskWithAttachments>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)
}