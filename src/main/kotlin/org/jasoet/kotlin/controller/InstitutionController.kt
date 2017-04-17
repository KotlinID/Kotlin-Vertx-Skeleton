package org.jasoet.kotlin.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.templ.TemplateEngine
import org.bson.types.ObjectId
import org.jasoet.kotlin.extension.OK
import org.jasoet.kotlin.extension.createQuery
import org.jasoet.kotlin.extension.getById
import org.jasoet.kotlin.extension.json
import org.jasoet.kotlin.extension.orNotFound
import org.jasoet.kotlin.extension.toValue
import org.jasoet.kotlin.extension.updateOperation
import org.jasoet.kotlin.model.Institution
import org.jasoet.kotlin.model.Location
import org.mongodb.morphia.Datastore
import javax.inject.Inject

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */

class InstitutionController @Inject constructor(override val router: Router,
                                                val datastore: Datastore,
                                                val mapper: ObjectMapper,
                                                val engine: TemplateEngine) : Controller({

    route().handler(BodyHandler.create())

    get("/").handler { context ->
        val institutions = datastore.createQuery<Institution>().toList()
        context.json(institutions)
    }

    get("/:id").handler { context ->
        val id = context.pathParam("id") orNotFound "Id is Blank!"
        val existingInstitution = datastore.getById<Institution>(ObjectId(id)) orNotFound "Location with id $id Not Found"
        context.json(existingInstitution)
    }

    post("/").handler { context ->
        val institution = mapper.toValue<Institution>(context.bodyAsJson)
        datastore.save(institution)

        context.OK("Successfully created")
    }

    put("/:id").handler { context ->
        val id = context.pathParam("id") orNotFound "Id is Blank!"
        val json = context.bodyAsJson

        val location = datastore.getById<Location>(ObjectId(json.getString("location")))

        val query = datastore.updateOperation<Institution>()
            .set("title", json.getString("title"))
            .set("subtitle", json.getString("subtitle"))
            .set("position", json.getString("position"))
            .set("address", json.getString("address"))
            .set("location", location)

        val institution = datastore.getById<Institution>(ObjectId(id))

        datastore.update(institution, query)

        context.OK("Successfully updated")
    }

    delete("/:id").handler { context ->
        val id = context.pathParam("id") orNotFound "Id is Blank!"
        val existingLocation = datastore.getById<Institution>(ObjectId(id)) orNotFound "Institution with id $id Not Found"
        datastore.delete(existingLocation)
        context.OK("Successfully Deleted")
    }
})
