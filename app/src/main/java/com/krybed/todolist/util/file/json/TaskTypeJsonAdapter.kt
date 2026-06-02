package com.krybed.todolist.util.file.json

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.krybed.todolist.data.model.TaskEntity
import com.krybed.todolist.data.model.enums.Priority
import com.krybed.todolist.domain.model.Task
import com.krybed.todolist.util.converter.Converters
import com.krybed.todolist.util.lang.LocalHelper
import java.io.IOException
import java.time.LocalDateTime

class TaskTypeJsonAdapter : TypeAdapter<Task>() {

    @Throws(IOException::class)
    override fun write(output: JsonWriter, task: Task?) {
        if (task == null) {
            output.nullValue()
            return
        }
        output.beginObject()
        output.name(Task.FIELD_ID).value(task.id)
        output.name(Task.FIELD_TITLE).value(task.title)
        output.name(Task.FIELD_DEADLINE).value(
            Converters.fromLocalDateTimeToString(task.deadline)
        )
        output.name(Task.FIELD_PRIORITY).value(task.priority.name)
        output.name(Task.FIELD_IS_DONE).value(task.isDone)
        output.name(Task.FIELD_CREATED_AT).value(
            Converters.fromLocalDateTimeToString(task.createdAt)
        )
        output.endObject()
    }

    @Throws(IOException::class)
    override fun read(input: JsonReader): Task {
//        var id = 0
        var title = ""
        var deadline = LocalDateTime.now().plusHours(24)
        var priority = Priority.HIGH
        var isDone = false
        var createdAtString: String? = null

        input.beginObject()
        while (input.hasNext()) {
            when (input.nextName()) {
//                Task.FIELD_ID -> id = input.nextInt()
                Task.FIELD_TITLE -> title = input.nextString()
                Task.FIELD_DEADLINE -> deadline = Converters.fromStringToLocalDateTime(
                    input.nextString()
                )
                Task.FIELD_PRIORITY -> priority = Priority.valueOf(input.nextString())
                Task.FIELD_IS_DONE -> isDone =  input.nextBoolean()
                Task.FIELD_CREATED_AT -> createdAtString = input.nextString()
                else -> input.skipValue()
            }
        }
        input.endObject()

        val createdAt = createdAtString?.let {
            Converters.fromStringToLocalDateTime(it)
        } ?: LocalDateTime.now()

        return Task(
//            id = id,
            title = title,
            deadline = deadline,
            isDone = isDone,
            priority = priority,
            createdAt = createdAt
        )
    }
}