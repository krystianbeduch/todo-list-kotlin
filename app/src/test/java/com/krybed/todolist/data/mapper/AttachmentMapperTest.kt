package com.krybed.todolist.data.mapper

import com.krybed.todolist.data.model.AttachmentEntity
import com.krybed.todolist.domain.model.Attachment
import org.junit.Assert.assertEquals
import org.junit.Assert
import org.junit.Test

class AttachmentMapperTest {

    @Test
    fun toDomain_shouldMapAttachmentEntityToAttachment() {
        val entity = AttachmentEntity(
            id = 1,
            taskId = 10,
            filename = "file.csv",
            filePath = "/tmp/file.csv"
        )

        val result = with(AttachmentMapper) { entity.toDomain() }

        assertEquals(1, result.id)
        assertEquals(10, result.taskId)
        assertEquals("file.csv", result.filename)
        assertEquals("/tmp/file.csv", result.filePath)
    }

    @Test
    fun toEntity_shouldMapAttachmentToAttachmentEntity() {
        val domain = Attachment(
            id = 2,
            taskId = 20,
            filename = "doc.json",
            filePath = "/storage/doc.json"
        )

        val result = with(AttachmentMapper) { domain.toEntity() }

        assertEquals(2, result.id)
        assertEquals(20, result.taskId)
        assertEquals("doc.json", result.filename)
        assertEquals("/storage/doc.json", result.filePath)
    }
}