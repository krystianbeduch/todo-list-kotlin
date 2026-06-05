package com.krybed.todolist.data.repository

import com.krybed.todolist.data.dao.AttachmentDao
import com.krybed.todolist.data.model.AttachmentEntity
import com.krybed.todolist.domain.model.Attachment
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AttachmentRepositoryImplTest {

    private lateinit var attachmentDao: AttachmentDao
    private lateinit var repository: AttachmentRepositoryImpl

    @Before
    fun setUp() {
        attachmentDao = mock()
        repository = AttachmentRepositoryImpl(attachmentDao)
    }

    @Test
    fun getByTaskId_shouldMapEntitiesToDomain() = runTest {
        whenever(attachmentDao.getByTaskId(1)).thenReturn(
            flowOf(
                listOf(
                    AttachmentEntity(
                        id = 1,
                        taskId = 1,
                        filename = "file.csv",
                        filePath = "/tmp/file.csv"
                    )
                )
            )
        )

        val result = repository.getByTaskId(1).first()

        assertEquals(1, result.size)
        assertEquals("file.csv", result.first().filename)
    }

    @Test
    fun insert_shouldMapDomainToEntityAndCallDao() = runTest {
        val attachment = Attachment(
            id = 1,
            taskId = 2,
            filename = "a.json",
            filePath = "/tmp/a.json"
        )

        repository.insert(attachment)

        val captor = argumentCaptor<AttachmentEntity>()
        verify(attachmentDao).insert(captor.capture())
        assertEquals(1, captor.firstValue.id)
        assertEquals(2, captor.firstValue.taskId)
        assertEquals("a.json", captor.firstValue.filename)
    }

    @Test
    fun delete_shouldMapDomainToEntityAndCallDao() = runTest {
        val attachment = Attachment(
            id = 5,
            taskId = 8,
            filename = "x.xml",
            filePath = "/tmp/x.xml"
        )

        repository.delete(attachment)

        val captor = argumentCaptor<AttachmentEntity>()
        verify(attachmentDao).delete(captor.capture())
        assertEquals(5, captor.firstValue.id)
        assertEquals("x.xml", captor.firstValue.filename)
    }
}