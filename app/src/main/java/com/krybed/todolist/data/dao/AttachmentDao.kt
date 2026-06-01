package com.krybed.todolist.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.krybed.todolist.data.model.Attachment

@Dao
interface AttachmentDao {

    @Query("SELECT * FROM Attachment")
    fun getAllAttachments(): List<Attachment>

    @Query("SELECT * FROM Attachment WHERE task_id = :taskId")
    fun getAttachmentsForTask(taskId: Int): List<Attachment>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insert(attachment: Attachment)

    @Delete
    fun delete(attachment: Attachment)
}