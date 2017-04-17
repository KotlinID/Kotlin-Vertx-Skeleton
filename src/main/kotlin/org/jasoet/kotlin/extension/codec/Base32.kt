package  org.jasoet.kotlin.extension.codec

import org.apache.commons.codec.binary.Base32
import org.apache.commons.codec.binary.StringUtils
import org.apache.commons.lang3.SerializationUtils
import java.io.Serializable


/**
 * Documentation
 *
 * @author Deny Prasetyo.
 */

private val base32Codec = Base32()

fun String.base32Encode(): String {
    return base32Codec.encodeToString(this.getBytesUtf8())
}

fun String.base32EncodeToByteArray(): ByteArray {
    return base32Codec.encode(this.getBytesUtf8())
}


fun <T : Serializable> T.base32Encode(): String {
    return base32Codec.encodeToString(this.serializeToByteArray())
}

fun <T : Serializable> T.base32EncodeToByteArray(): ByteArray {
    return base32Codec.encode(this.serializeToByteArray())
}

fun ByteArray.base32EncodeToString(): String {
    return base32Codec.encodeAsString(this)
}

fun ByteArray.base32Encode(): ByteArray {
    return base32Codec.encode(this)
}

@Throws(DecodeBase32Exception::class)
fun String.base32Decode(): String {
    return try {
        StringUtils.newStringUtf8(base32Codec.decode(this))
    } catch (e: Exception) {
        throw  DecodeBase32Exception("Exception when Decode ${this.abbreviate()} ", e)
    }

}

@Throws(DecodeBase32Exception::class)
fun <T : Serializable> String.base32DecodeToObject(): T {
    return try {
        SerializationUtils.deserialize<T>(base32Codec.decode(this))
    } catch (e: Exception) {
        throw  DecodeBase32Exception("Exception when Decode ${this.abbreviate()} ", e)
    }
}

@Throws(DecodeBase32Exception::class)
fun <T : Serializable> ByteArray.base32DecodeToObject(): T {
    return try {
        SerializationUtils.deserialize<T>(base32Codec.decode(this))
    } catch (e: Exception) {
        throw  DecodeBase32Exception("Exception when Decode ${this.size} bytes ", e)
    }
}


class DecodeBase32Exception(message: String, cause: Throwable) : Exception(message, cause)