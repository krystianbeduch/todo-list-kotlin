package com.krybed.todolist.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.krybed.todolist.data.model.Task

@Dao
interface TaskDao {

    @Query("SELECT * FROM Task ORDER BY created_at DESC")
    fun getTasksOrderDescByCreatedDate(): LiveData<List<Task>>

    @Query("SELECT * FROM Task")
    fun getAllSync(): List<Task>

    @Query("SELECT * FROM Task WHERE id = :id")
    fun getById(id: Int): LiveData<Task?>

    @Insert
    fun insert(task: Task)

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)

    @Query("DELETE FROM Task")
    fun deleteAll()

    @Query("UPDATE Task SET is_done = :isDone WHERE id = :taskId")
    fun changeStatus(taskId: Int, isDone: Boolean)
}