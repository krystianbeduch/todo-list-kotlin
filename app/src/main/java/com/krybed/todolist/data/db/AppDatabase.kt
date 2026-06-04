package com.krybed.todolist.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.krybed.todolist.data.dao.AttachmentDao
import com.krybed.todolist.data.dao.TaskDao
import com.krybed.todolist.data.model.TaskEntity
import com.krybed.todolist.data.model.AttachmentEntity
import com.krybed.todolist.util.converter.Converters

@Database(
    entities = [TaskEntity::class, AttachmentEntity::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
    abstract fun attachmentDao(): AttachmentDao

    companion object {
        private const val DATABASE_NAME = "task_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(ctx: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(
                    ctx.applicationContext
                ).also { INSTANCE = it }
            }

        private fun buildDatabase(ctx: Context): AppDatabase =
            Room.databaseBuilder(
                ctx,
                AppDatabase::class.java,
                DATABASE_NAME
            ).fallbackToDestructiveMigration(false).build()
    }
}