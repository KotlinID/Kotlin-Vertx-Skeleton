package org.jasoet.kotlin.model

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import io.vertx.core.json.JsonObject
import org.bson.types.ObjectId
import org.hibernate.validator.constraints.NotBlank
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Field
import org.mongodb.morphia.annotations.Id
import org.mongodb.morphia.annotations.Index
import org.mongodb.morphia.annotations.IndexOptions
import org.mongodb.morphia.annotations.Indexes

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */

@Entity("locations")
@Indexes(
    Index(value = "province", fields = arrayOf(Field("province")), options = IndexOptions(unique = true))
)
class Location() {
    constructor(
        id: ObjectId? = null,
        province: String = "",
        city: List<String> = emptyList(),
        obj: JsonObject? = null
    ) : this() {
        this.id = id
        this.province = province
        this.city = city
        this.obj = obj
    }

    @Id
    @JsonSerialize(using = ToStringSerializer::class)
    var id: ObjectId? = null
    @NotBlank
    var province: String = ""
    @NotBlank
    var city: List<String> = emptyList()
    @NotBlank
    var obj: JsonObject? = null
}