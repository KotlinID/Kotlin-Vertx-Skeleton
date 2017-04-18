package org.jasoet.kotlin

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.unit.TestContext
import io.vertx.ext.unit.junit.VertxUnitRunner
import org.jasoet.kotlin.extension.getById
import org.jasoet.kotlin.extension.logger
import org.jasoet.kotlin.extension.observableCall
import org.jasoet.kotlin.extension.propertiesConfig
import org.jasoet.kotlin.extension.retrieveConfig
import org.jasoet.kotlin.model.Location
import org.jasoet.kotlin.module.DaggerTestAppComponent
import org.jasoet.kotlin.module.MongoModule
import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mongodb.morphia.Datastore
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo
 */

@RunWith(VertxUnitRunner::class)
class JsonObjectTest {
    val log = logger(JsonObjectTest::class)
    lateinit var datastore: Datastore
    lateinit var vertx: Vertx

    @Before
    fun setUp(context: TestContext) {
        log.info("Initialize Components")
        vertx = Vertx.vertx()
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
                    datastore = app.dataStore()
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
    fun testInsertAndLoadData(context: TestContext) {
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
        val keyLoc = datastore.save(location)
        log.info("Save Should be Success")
        val locationFromDb = datastore.getById<Location>(keyLoc.id)
        assertNotNull(locationFromDb)
        log.info("Load Should Success Also")
    }

    @After
    fun tearDown(context: TestContext) {
        vertx.close(context.asyncAssertSuccess())
    }
}