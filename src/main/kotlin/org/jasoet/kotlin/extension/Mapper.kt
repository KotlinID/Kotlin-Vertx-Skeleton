package org.jasoet.kotlin.extension

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.json.JsonObject

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */


inline fun <reified T : Any> ObjectMapper.toValue(jsonString: String): T {
    return this.convertValue(jsonString, T::class.java)
}

inline fun <reified T : Any> ObjectMapper.toValue(json: JsonObject): T {
    return this.convertValue(json.map, T::class.java)
}

inline fun <reified T : Any> ObjectMapper.toJson(obj: T): JsonObject {
    val jsonString = this.writeValueAsString(obj)
    return JsonObject(jsonString)
}
