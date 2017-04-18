package org.jasoet.kotlin.controller

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.jasoet.kotlin.extension.logger
import org.jasoet.kotlin.extension.observableCall
import org.jasoet.kotlin.extension.propertiesConfig
import org.jasoet.kotlin.extension.retrieveConfig
import org.jasoet.kotlin.module.DaggerTestAppComponent
import org.jasoet.kotlin.module.MongoModule
import org.jasoet.kotlin.module.VertxModule
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.net.ServerSocket
import kotlin.test.assertTrue

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo
 */
@RunWith(VertxUnitRunner::class)
class MainControllerTest {
    val log = logger(MainControllerTest::class)
    lateinit var vertx: Vertx
    lateinit var sharedConfig: JsonObject
    var port: Int = 0

    @Before
    fun setUp(context: TestContext) {
        log.info("Initialize Components")

        val socket = ServerSocket(0)
        port = socket.localPort
        socket.close()

        vertx = Vertx.vertx()
        val properties = propertiesConfig("application-config.properties")
        vertx.retrieveConfig(properties)
            .map { it.put("HTTP_PORT", port) }
            .map { it to vertx }
            .doOnError {
                log.error("Error Occurred when deploying/retrieving config ${it.message}", it)
            }
            .flatMap {
                val (config, vertx) = it
                log.info("Initialize Components...")
                sharedConfig = config
                observableCall {
                    val app = DaggerTestAppComponent.builder()
                        .vertxModule(VertxModule(vertx, config))
                        .mongoModule(MongoModule(config))
                        .build()
                    val deployOption = DeploymentOptions().apply {
                        this.config = config
                    }
                    vertx.deployVerticle(app.mainVerticle(), deployOption, context.asyncAssertSuccess())
                }
            }
            .doOnError {
                assertTrue(false, "Failed due to ${it.message}")
                log.error("${it.message} occurred when Saving Document!", it)
            }
            .doOnCompleted {
                assertTrue(true, "Initialize Components Success")
            }
            .subscribe()
    }

    @Test
    fun testSimpleEndpoint(context: TestContext) {
        val async = context.async()
        val port = sharedConfig.getInteger("HTTP_PORT")

        log.info("Request Get to localhost:$port/")
        vertx.createHttpClient().getNow(port, "localhost", "/") { response ->
            response.handler { body ->
                val bodyAsString = body.toString()
                log.info("Received Body [$bodyAsString]")
                context.assertTrue(bodyAsString.contains("Hello", ignoreCase = true))
                async.complete()
            }
        }
    }

    @After
    fun tearDown(context: TestContext) {
        vertx.close(context.asyncAssertSuccess())
    }
}