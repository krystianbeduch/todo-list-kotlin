package com.krybed.todolist.util.file.xml

import org.simpleframework.xml.Default
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "Tasks", strict = false)
@Default
class TaskXmlWrapper() {

    @field:ElementList(name = "Tasks", entry = "Task", inline = true, required = false)
    var taskXmlList: MutableList<TaskXml> = mutableListOf()

    constructor(taskXmlList: MutableList<TaskXml>) : this() {
        this.taskXmlList = taskXmlList
    }
}