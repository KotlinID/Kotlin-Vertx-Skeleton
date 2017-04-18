package org.jasoet.kotlin

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import org.jasoet.kotlin.extension.getById
import org.jasoet.kotlin.extension.logger
import org.jasoet.kotlin.extension.observableCall
import org.jasoet.kotlin.extension.propertiesConfig
import org.jasoet.kotlin.extension.retrieveConfig
import org.jasoet.kotlin.model.Location
import org.jasoet.kotlin.module.DaggerAppComponent
import org.jasoet.kotlin.module.DaggerTestAppComponent
import org.jasoet.kotlin.module.MongoModule
import org.jasoet.kotlin.module.VertxModule
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo
 */

class JsonObjectTest {
    val log = logger(JsonObjectTest::class)

    @Test
    fun testInsertAndLoadData() {
        log.info("Initialize Components")
        val vertx = Vertx.vertx()
        val properties = propertiesConfig("application-config.properties")
        vertx.retrieveConfig(properties)
            .map { it to vertx }
            .doOnError {
                log.error("Error Occurred when deploying/retrieving config ${it.message}", it)
            }
            .flatMap {
                val (config, _) = it
                log.info("Initialize Components...")
                observableCall {
                    val app = DaggerTestAppComponent.builder()
                        .mongoModule(MongoModule(config))
                        .build()
                    val jsonString = """
                            {
                                "title": "Person",
                                "type": "object",
                                "properties": {
                                    "firstName": {
                                        "type": "string"
                                    },
                                    "lastName": {
                                        "type": "string"
                                    },
                                    "age": {
                                        "description": "Age in years",
                                        "type": "integer",
                                        "minimum": 0
                                    }
                                },
                                "required": ["firstName", "lastName"]
                            }
                            """.trimMargin()

                    val jsonObject = JsonObject(jsonString)
                    val location = Location(province = "DIY", city = listOf("Bantul", "Sleman"), obj = jsonObject)
                    val dataStore = app.dataStore()
                    val keyLoc = dataStore.save(location)
                    log.info("Save Should be Success")
                    val locationFromDb = dataStore.getById<Location>(keyLoc.id)
                    assertNotNull(locationFromDb)
                    log.info("Load Should Success Also")
                }
            }
            .doOnError {
                assertTrue(false,"Failed due to ${it.message}")
                log.error("${it.message} occurred when Saving Document!", it)
            }
            .subscribe()
    }
}