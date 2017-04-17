package org.jasoet.kotlin

import com.hazelcast.config.Config
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.logging.LoggerFactory
import io.vertx.core.logging.SLF4JLogDelegateFactory
import io.vertx.core.spi.cluster.ClusterManager
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager
import org.jasoet.kotlin.extension.env
import org.jasoet.kotlin.extension.logger
import org.jasoet.kotlin.extension.observable
import org.jasoet.kotlin.extension.observableCall
import org.jasoet.kotlin.extension.propertiesConfig
import org.jasoet.kotlin.extension.retrieveConfig
import org.jasoet.kotlin.module.DaggerAppComponent
import org.jasoet.kotlin.module.MongoModule
import org.jasoet.kotlin.module.VertxModule
import java.net.InetAddress


/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo
 */

class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            System.setProperty(LoggerFactory.LOGGER_DELEGATE_FACTORY_CLASS_NAME, SLF4JLogDelegateFactory::class.java.name)
            val log = logger(Application::class)

            val hazelcastConfig = Config()

            val hazelManager: ClusterManager = HazelcastClusterManager(hazelcastConfig)

            val vertxOption = VertxOptions().apply {
                this.clusterManager = hazelManager
                try {
                    val address = InetAddress.getByName(env("HOSTNAME", "localhost")).hostAddress
                    this.clusterHost = address
                    log.info("Cluster set to use clusterHost ${this.clusterHost}")
                } catch (e: Exception) {
                    log.info("Hostname not Found, perhaps you run this app locally!")
                }
            }

            observable<Vertx> { Vertx.clusteredVertx(vertxOption, it) }
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
