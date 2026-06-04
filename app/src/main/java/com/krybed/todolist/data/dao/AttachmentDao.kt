package com.krybed.todolist.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krybed.todolist.data.model.AttachmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachmentDao {

    @Query("SELECT * FROM AttachmentEntity WHERE task_id = :taskId")
    fun getByTaskId(taskId: Int): Flow<List<AttachmentEntity>>

    @Query("SELECT * FROM AttachmentEntity WHERE task_id = :taskId")
    suspend fun getByTaskIdOnce(taskId: Int): List<AttachmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attachment: AttachmentEntity)

    @Delete
    suspend fun delete(attachment: AttachmentEntity)
}