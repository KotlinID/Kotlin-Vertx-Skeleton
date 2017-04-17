package  org.jasoet.kotlin.extension.codec

import org.apache.commons.codec.binary.Base64
import org.apache.commons.lang3.SerializationUtils
import org.apache.commons.lang3.StringUtils
import java.io.Serializable
import org.apache.commons.codec.binary.StringUtils as CodecStringUtils


/**
 * Documentation
 *
 * @author Deny Prasetyo.
 */

private val base64Codec = Base64()

fun String.abbreviate(width: Int = 42): String {
    return StringUtils.abbreviate(this, width)
}

fun String.getBytesUtf8(): ByteArray {
    return this.getBytesUtf8()
}

fun <T : Serializable> T.serializeToByteArray(): ByteArray {
    return SerializationUtils.serialize(this)
}

fun String.base64Encode(): String {
    return base64Codec.encodeToString(this.getBytesUtf8())
}

fun String.base64EncodeToByteArray(): ByteArray {
    return base64Codec.encode(this.getBytesUtf8())
}

fun <T : Serializable> T.base64Encode(): String {
    return base64Codec.encodeToString(this.serializeToByteArray())
}

fun <T : Serializable> T.base64EncodeToByteArray(): ByteArray {
    return base64Codec.encode(this.serializeToByteArray())
}

fun ByteArray.base64EncodeToString(): String {
    return base64Codec.encodeAsString(this)
}

fun ByteArray.base64Encode(): ByteArray {
    return base64Codec.encode(this)
}

@Throws(DecodeBase64Exception::class)
fun String.base64Decode(): String {
    return try {
        org.apache.commons.codec.binary.StringUtils.newStringUtf8(base64Codec.decode(this))
    } catch (e: Exception) {
        throw  DecodeBase64Exception("Exception when Decode ${this.abbreviate()}", e)
    }
}

@Throws(DecodeBase64Exception::class)
fun <T : Serializable> String.base64DecodeToObject(): T {
    return try {
        SerializationUtils.deserialize<T>(base64Codec.decode(this))
    } catch (e: Exception) {
        throw  DecodeBase64Exception("Exception when Decode ${this.abbreviate()}", e)
    }
}

@Throws(DecodeBase64Exception::class)
fun <T : Serializable> ByteArray.base64DecodeToObject(): T {
    return try {
        SerializationUtils.deserialize<T>(base64Codec.decode(this))
    } catch (e: Exception) {
        throw  DecodeBase64Exception("Exception when Decode ${this.size} bytes ", e)
    }
}


class DecodeBase64Exception(message: String, cause: Throwable) : Exception(message, cause)