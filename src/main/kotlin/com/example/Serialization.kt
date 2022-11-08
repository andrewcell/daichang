package com.example

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/*object EquipmentSerializer : JsonContentPolymorphicSerializer<Equipment>(Equipment::class) {
    ove0rride fun selectDeserializer(element: JsonElement) = when {

    }
}*/

object EncryptSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("EncryptString", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): String {
        return if (System.getenv("io.ktor.development")?.toString()?.lowercase() == "true" || System.getenv("daichang_disable_encryption")?.toString()?.lowercase() == "true") {
            decoder.decodeString()
        } else {
            decoder.decodeString().decryptECB()
        }
    }

    private fun String.encryptECB(): String {
        val keySpec = SecretKeySpec("1234567890123456".toByteArray(), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, keySpec)
        val ciphertext = cipher.doFinal(this.toByteArray())
        val encodedByte = Base64.getEncoder().encode(ciphertext)
        return String(encodedByte)
    }

    private fun String.decryptECB(): String {
        val keySpec = SecretKeySpec("1234567890123456".toByteArray(), "AES")
        val decodedByte: ByteArray = Base64.getDecoder().decode(this)
        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, keySpec)
        val output = cipher.doFinal(decodedByte)
        return String(output)
    }
    override fun serialize(encoder: Encoder, value: String) {
        return if (System.getenv("io.ktor.development")?.toString()?.lowercase() == "true" || System.getenv("daichang_disable_encryption")?.toString()?.lowercase() == "true") {
            encoder.encodeString(value)
        } else {
            encoder.encodeString(value.encryptECB())
        }
    }

}

object LocalDateSerializer : KSerializer<LocalDate> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDate", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeLong(value.toEpochDay())
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.ofEpochDay(decoder.decodeLong())
    }
}