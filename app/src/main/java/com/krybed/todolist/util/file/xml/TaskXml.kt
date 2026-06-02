package com.krybed.todolist.util.file.xml

import com.krybed.todolist.data.model.enums.Priority
import com.krybed.todolist.domain.model.Task
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
        return Task(
            id = id,
            title = title,
            deadline = Converters.fromStringToLocalDateTime(deadline),
            priority = priority,
            isDone = isDone,
            createdAt = Converters.fromStringToLocalDateTime(createdAt)
        )
    }
}