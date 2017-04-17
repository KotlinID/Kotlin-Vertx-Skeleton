package org.jasoet.kotlin.extension.codec

import org.apache.commons.codec.digest.DigestUtils
import java.io.InputStream

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo
 */

/**
 * Calculate SHA1 Digest as Hexadecimal
 * Do not close [InputStream] after use nor reset
 * please handle it yourself
 *
 * @return Hexadecimal Digest from [InputStream] as String
 */
fun InputStream.sha1HexDigest(): String {
    return DigestUtils.sha1Hex(this)
}

/**
 * Calculate SHA1 Digest as Hexadecimal
 *
 * @return Hexadecimal Digest from [ByteArray] as String
 */
fun ByteArray.sha1HexDigest(): String {
    return DigestUtils.sha1Hex(this)
}
