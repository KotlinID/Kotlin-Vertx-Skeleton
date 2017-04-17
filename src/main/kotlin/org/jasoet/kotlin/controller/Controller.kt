package org.jasoet.kotlin.controller

import io.vertx.ext.web.Router

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */


abstract class Controller(val handlers: Router.() -> Unit) {
    abstract val router: Router
    fun create(): Router {
        return router.apply {
            handlers()
        }
    }
}