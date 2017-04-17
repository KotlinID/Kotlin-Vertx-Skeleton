package org.jasoet.kotlin.controller

import com.fasterxml.jackson.databind.ObjectMapper
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import org.bson.types.ObjectId
import org.jasoet.kotlin.extension.byJson
import org.jasoet.kotlin.extension.createQuery
import org.jasoet.kotlin.extension.getById
import org.jasoet.kotlin.extension.json
import org.jasoet.kotlin.extension.orNotFound
import org.jasoet.kotlin.extension.param
import org.jasoet.kotlin.extension.toValue
import org.jasoet.kotlin.extension.updateOperation
import org.jasoet.kotlin.model.Location
import org.mongodb.morphia.Datastore
import java.util.regex.Pattern
import javax.inject.Inject

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */


class LocationController @Inject constructor(override val router: Router,
                                             val datastore: Datastore,
                                             val mapper: ObjectMapper) : Controller({

    route().handler(BodyHandler.create())

    get("/").handler { context ->
        val user = context.user()
        user.principal()
        val locations = datastore.createQuery<Location>().toList()

        val json = JsonObject(mapOf(
            "status" to true,
            "message" to "Data Found",
            "data" to locations
        ))

        context.json(json)
    }

    get("/search").handler { context ->
        val city = context.param("city")
        val regexp = Pattern.compile(city)
        val query = datastore.createQuery<Location>().filter("city", regexp).asList()

        val json = JsonObject(mapOf(
            "status" to true,
            "message" to "Data Found",
            "data" to query
        ))

        context.json(json)
    }

    post("/").handler { context ->
        val location = mapper.toValue<Location>(context.bodyAsJson)
        datastore.save(location)

        val json = JsonObject(mapOf(
            "status" to true,
            "message" to "Successfully created",
            "data" to location
        ))

        context.json(json)
    }

    put("/:id").handler { context ->
        val id = context.pathParam("id") orNotFound "Id is Blank!"
        val body = context.bodyAsJson
        val query = datastore.updateOperation<Location>().byJson(body)
        val existingLocation = datastore.getById<Location>(ObjectId(id)) orNotFound "Location with id $id Not Found"

        datastore.update(existingLocation, query)

        val json = JsonObject(mapOf(
            "status" to true,
            "message" to "Successfully updated"
        ))

        context.json(json)
    }

    delete("/:id").handler { context ->
        val id = context.pathParam("id") orNotFound "Id is Blank!"
        val existingLocation = datastore.getById<Location>(ObjectId(id)) orNotFound "Location with id $id Not Found"
        datastore.delete(existingLocation)

        val json = JsonObject(mapOf(
            "status" to true,
            "message" to "Successfully Deleted"
        ))

        context.json(json)
    }

    get("/:id").handler { context ->
        val id = context.pathParam("id") orNotFound "Id is Blank!"
        val existingLocation = datastore.getById<Location>(ObjectId(id)) orNotFound "Location with id $id Not Found"

        val json = JsonObject(mapOf(
            "status" to true,
            "message" to "Successfully Deleted",
            "data" to existingLocation
        ))

        context.json(json)
    }
})