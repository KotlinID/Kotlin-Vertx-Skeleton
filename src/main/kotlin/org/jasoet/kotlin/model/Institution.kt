package org.jasoet.kotlin.model

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.bson.types.ObjectId
import org.hibernate.validator.constraints.NotBlank
import org.mongodb.morphia.annotations.Entity
import org.mongodb.morphia.annotations.Field
import org.mongodb.morphia.annotations.Id
import org.mongodb.morphia.annotations.Index
import org.mongodb.morphia.annotations.IndexOptions
import org.mongodb.morphia.annotations.Indexes
import org.mongodb.morphia.annotations.Reference
import java.time.LocalDate

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */

@Entity("institutions")
@Indexes(
    Index(value = "title", fields = arrayOf(Field("title")), options = IndexOptions(unique = true)),
    Index(value = "title_subtitle", fields = arrayOf(Field("title"), Field("subtitle")), options = IndexOptions(unique = true))
)
class Institution() {
    constructor(
        id: ObjectId? = null,
        title: String,
        subtitle: String = "",
        position: String = "",
        address: String = "",
        location: Location,
        updatedOn: LocalDate? = null,
        updatedBy: String? = null
    ) : this() {
        this.id = id
        this.title = title
        this.subtitle = subtitle
        this.position = position
        this.address = address
        this.location = location
        this.updatedOn = updatedOn
        this.updatedBy = updatedBy
    }

    @Id
    @JsonSerialize(using = ToStringSerializer::class)
    var id: ObjectId? = null
    @NotBlank
    lateinit var title: String
    @NotBlank
    var subtitle: String = ""
    var position: String = ""
    var address: String = ""
    @Reference
    lateinit var location: Location
    @JsonSerialize(using = ToStringSerializer::class)
    var updatedOn: LocalDate? = null
    var updatedBy: String? = null
}