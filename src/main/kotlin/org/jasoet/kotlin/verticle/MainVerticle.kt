package org.jasoet.kotlin.verticle

import io.vertx.core.AbstractVerticle
import io.vertx.core.Future
import io.vertx.core.http.HttpServer
import org.jasoet.kotlin.controller.MainController
import org.jasoet.kotlin.extension.futureTask
import org.jasoet.kotlin.extension.handle
import org.jasoet.kotlin.extension.logger
import javax.inject.Inject

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */

class MainVerticle @Inject constructor(val mainController: MainController) : AbstractVerticle() {
    private val log = logger(MainVerticle::class)
    private val config by lazy { config() }

    override fun start(startFuture: Future<Void>) {
        log.info("Initialize Main Verticle...")

        log.info("Initialize Router...")
        val router = mainController.create()

        log.info("Starting HttpServer...")
        val httpServer = futureTask<HttpServer> {
            vertx.createHttpServer()
                .requestHandler { router.accept(it) }
                .listen(config.getInteger("HTTP_PORT"), it)
        }

        httpServer.handle(
            {
                startFuture.complete()
                log.info("HttpServer started in port ${config.getInteger("HTTP_PORT")}")
                log.info("Main Verticle Deployed!")
            },
            {
                startFuture.fail(it)
                log.error("Failed to start HttpServer. [${it.message}]", it)
                log.error("Main Verticle Failed to Deploy!")
            }
        )
    }
}