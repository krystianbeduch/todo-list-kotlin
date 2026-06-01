package com.krybed.todolist.util.file.xml

import com.krybed.todolist.data.model.Task
import com.krybed.todolist.data.model.enums.Priority
import com.krybed.todolist.util.converter.Converters
import org.simpleframework.xml.Default
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "Task")
@Default
class TaskXml() {

    @field:Element(required = false)
    var id: Int = 0

    @field:Element
    var title: String = ""

    @field:Element
    var deadline: String = ""

    @field:Element
    var priority: Priority = Priority.LOW

    @field:Element
    var isDone: Boolean = false

    @field:Element
    var createdAt: String = ""

    constructor(task: Task) : this() {
        id = task.id
        title = task.title
        deadline = Converters.fromLocalDateTimeToString(task.deadline).toString()
        priority = task.priority
        isDone = task.isDone
        createdAt = Converters.fromLocalDateTimeToString(task.createdAt).toString()
    }

    fun toTask(): Task {
        return Task().apply {
            title = this@TaskXml.title
            deadline = Converters.fromStringToLocalDateTime(this@TaskXml.deadline)
            priority = this@TaskXml.priority
            isDone = this@TaskXml.isDone
            createdAt = Converters.fromStringToLocalDateTime(this@TaskXml.createdAt)
        }
    }
}