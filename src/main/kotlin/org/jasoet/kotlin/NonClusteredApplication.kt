package org.jasoet.kotlin

import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.logging.SLF4JLogDelegateFactory
import org.jasoet.kotlin.extension.logger
import org.jasoet.kotlin.extension.observableCall
import org.jasoet.kotlin.extension.propertiesConfig
import org.jasoet.kotlin.extension.retrieveConfig
import org.jasoet.kotlin.module.DaggerAppComponent
import org.jasoet.kotlin.module.MongoModule
import org.jasoet.kotlin.module.VertxModule


/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo
 */

class NonClusteredApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory::class.java.name)
            val log = logger(Application::class)


            observableCall<Vertx> { Vertx.vertx() }
                .flatMap {
                    val vertx = it
                    val properties = propertiesConfig("application-config.properties")
                    vertx.retrieveConfig(properties)
                        .map { it to vertx }
                }
                .doOnError {
                    log.error("Error Occurred when deploying/retrieving config ${it.message}", it)
                }
                .flatMap {
                    val (config, vertx) = it
                    log.info("Initialize Components...")
                    observableCall {
                        val app = DaggerAppComponent.builder()
                            .vertxModule(VertxModule(vertx, config))
                            .mongoModule(MongoModule(config))
                            .build()

                        log.info("Execute Data Initializer")
                        val initializer = app.initializer()
                        initializer()

                        log.info("Deploying Main Verticle")
                        val mainVerticle = app.mainVerticle()
                        vertx.deployVerticle(mainVerticle, DeploymentOptions().apply {
                            this.config = config
                        })
                    }
                }
                .doOnError {
                    log.error("${it.message} when Deploy Verticles!", it)
                }
                .subscribe()
        }
    }
}
