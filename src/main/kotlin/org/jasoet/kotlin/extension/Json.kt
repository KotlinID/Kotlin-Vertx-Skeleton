package org.jasoet.kotlin.extension

import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import org.mongodb.morphia.converters.TypeConverter
import org.mongodb.morphia.mapping.MappedField

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo
 */

class JsonObjectConverter : TypeConverter(JsonObject::class.java) {
    override fun decode(targetClass: Class<*>, fromDBObject: Any, optionalExtraInfo: MappedField?): Any {
        return if (fromDBObject is Map<*, *>) {
            val jsonEncode = Json.encode(fromDBObject)
            JsonObject(jsonEncode)
        } else if (fromDBObject is String) {
            JsonObject(fromDBObject)
        } else {
            JsonObject()
        }
    }

    override fun encode(value: Any?, optionalExtraInfo: MappedField?): Any? {
        return if (value is JsonObject) {
            value.map
        } else {
            value
        }
    }
}
