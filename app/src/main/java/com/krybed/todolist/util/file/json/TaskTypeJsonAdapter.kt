package com.krybed.todolist.util.file.json

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import com.krybed.todolist.data.model.Task
import com.krybed.todolist.data.model.enums.Priority
import com.krybed.todolist.util.converter.Converters
import java.io.IOException

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
        val task = Task()
        input.beginObject()
        while (input.hasNext()) {
            when (input.nextName()) {
                Task.FIELD_TITLE -> task.title = input.nextString()
                Task.FIELD_DEADLINE -> task.deadline = Converters.fromStringToLocalDateTime(
                    input.nextString()
                )
                Task.FIELD_PRIORITY -> task.priority = Priority.valueOf(input.nextString())
                Task.FIELD_IS_DONE -> task.isDone =  input.nextBoolean()
                Task.FIELD_CREATED_AT -> task.createdAt = Converters.fromStringToLocalDateTime(
                    input.nextString()
                )
                else -> input.skipValue()
            }
        }
        input.endObject()
        return task
    }
}