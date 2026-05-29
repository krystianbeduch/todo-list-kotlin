package com.krybed.todolist.data.model.enums

enum class FileType (
    val mimeType: String,
    val extension: String
) {
    CSV("text/csv", ".csv"),
    JSON("application/json", ".json"),
    XML("application/xml", ".xml")
}