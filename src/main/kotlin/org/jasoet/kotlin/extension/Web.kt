package org.jasoet.kotlin.extension

import io.netty.handler.codec.http.HttpResponseStatus
import io.vertx.core.Handler
import io.vertx.core.http.HttpServerResponse
import io.vertx.core.json.Json
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.templ.TemplateEngine

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */

fun Route.first(): Route {
    return this.order(-100)
}

@Suppress("NOTHING_TO_INLINE")
inline fun Route.render(engine: TemplateEngine, template: String): Route {
    return this.handler { context ->
        context.render(engine, template)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun Route.reroute(destination: String): Route {
    return this.handler { context ->
        context.reroute(destination)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun Route.serveStatic(): Route {
    return this.handler(io.vertx.ext.web.handler.StaticHandler.create())
}


@Suppress("NOTHING_TO_INLINE")
inline fun Route.serveStatic(webRoot: String): Route {
    return this.handler(io.vertx.ext.web.handler.StaticHandler.create().apply {
        setWebRoot(webRoot)
    })
}

/**
 * Extension to the HTTP response to output JSON objects.
 */
fun HttpServerResponse.endWithJson(obj: Any) {
    this.putHeader("Content-Type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(obj))
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.header(key: String): String? {
    return this.request().headers().get(key)
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.param(key: String): String? {
    return this.request().getParam(key)
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.json(obj: Any) {
    val response = this.response()
    response.putHeader("Content-Type", "application/json; charset=utf-8")
        .end(Json.encode(obj))
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.json(headers: Map<String, String> = emptyMap(), message: Any) {
    this.response().apply {
        headers.entries.fold(this) { response, entries ->
            response.putHeader(entries.key, entries.value)
        }
        putHeader("Content-Type", "application/json; charset=utf-8")
        end(Json.encode(message))
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.jsonBody(): JsonObject? {
    val result: JsonObject? = try {
        this.bodyAsJson
    } catch (e: Exception) {
        null
    }
    return result
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.jsonArrayBody(): JsonArray? {
    val result: JsonArray? = try {
        this.bodyAsJsonArray
    } catch (e: Exception) {
        null
    }
    return result
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.OK(message: String = "", headers: Map<String, String> = emptyMap()) {
    this.response().let {
        it.statusCode = HttpResponseStatus.OK.code()
        headers.entries.fold(it) { response, entries ->
            response.putHeader(entries.key, entries.value)
        }
        it.end(message)
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.prettyJson(obj: Any) {
    val response = this.response()
    response.putHeader("Content-Type", "application/json; charset=utf-8")
        .end(Json.encodePrettily(obj))
}

inline fun handler(crossinline handle: (RoutingContext) -> Unit): Handler<RoutingContext> {
    return Handler { handle(it) }
}

@Suppress("NOTHING_TO_INLINE")
inline fun RoutingContext.render(engine: TemplateEngine, templateName: String) {
    engine.render(this, templateName, {
        if (it.succeeded()) {
            this.response().end(it.result())
        } else {
            this.fail(it.cause())
        }
    })
}