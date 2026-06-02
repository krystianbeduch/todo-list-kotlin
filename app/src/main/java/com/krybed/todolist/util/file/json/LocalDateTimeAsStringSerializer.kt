package com.krybed.todolist.util.file.json

import com.krybed.todolist.util.converter.Converters
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime

object LocalDateTimeAsStringSerializer : KSerializer<LocalDateTime> {

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: LocalDateTime
    ) {
        encoder.encodeString(
            Converters.fromLocalDateTimeToString(value) ?: ""
        )
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return Converters.fromStringToLocalDateTime(
            decoder.decodeString()
        )
    }
}