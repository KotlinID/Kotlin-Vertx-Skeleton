package org.jasoet.kotlin.module

import com.mongodb.MongoClient
import com.mongodb.ServerAddress
import com.mongodb.client.MongoDatabase
import dagger.Module
import dagger.Provides
import io.vertx.core.json.JsonObject
import org.jasoet.kotlin.extension.JsonObjectConverter
import org.jasoet.kotlin.extension.count
import org.jasoet.kotlin.extension.createQuery
import org.jasoet.kotlin.extension.logger
import org.jasoet.kotlin.model.Institution
import org.jasoet.kotlin.model.Location
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Morphia
import org.mongodb.morphia.mapping.Mapper
import javax.inject.Named
import javax.inject.Singleton

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */

@Module
class MongoModule(val config: JsonObject) {
    private val log = logger(MongoModule::class)

    @Provides
    @Singleton
    fun provideMongoClient(): MongoClient {
        val host = config.getString("MONGODB_HOST")
        val port = config.getInteger("MONGODB_PORT")
        val server = ServerAddress(host, port)

        val client = MongoClient(server)

        val address = try {
            log.info("Trying to Connect MongoDB Database [$host:$port]...")
            client.address
        } catch (e: Exception) {
            log.error("Mongo is not Connected [${e.message}]", e)
            client.close()
            throw e
        }

        log.info("MongoDB is Connected to $address")
        return client
    }

    @Provides
    @Singleton
    fun provideMongoDatabase(mongoClient: MongoClient): MongoDatabase {
        val databaseName = config.getString("MONGODB_DATABASE")
        return mongoClient.getDatabase(databaseName)
    }

    @Provides
    @Singleton
    fun providesMorphia(): Morphia {
        val mapper = Mapper().apply {
            with(converters) {
                addConverter(JsonObjectConverter())
            }
        }
        return Morphia(mapper).apply {
            mapPackage("org.jasoet.kotlin.model")
        }
    }

    @Provides
    @Singleton
    fun provideMorphiaDataStore(morphia: Morphia, client: MongoClient): Datastore {
        return morphia
            .createDatastore(client, config.getString("MONGODB_DATABASE"))
            .apply {
                ensureIndexes()
            }
    }

    @Provides
    @Singleton
    @Named("dataInitializer")
    fun provideInitializer(datastore: Datastore): () -> Unit {
        val operation: () -> Unit = {

            if (datastore.count<Location>() == 0L) {
                datastore.save(
                    listOf(
                        Location(province = "D.I.Yogyakarta", city = listOf("Bantul", "Sleman", "Yogyakarta", "Kulon Progo"))
                    )
                )
            }


            if (datastore.count<Institution>() == 0L) {
                val location = datastore.createQuery<Location>()
                    .filter("province =", "D.I.Yogyakarta")
                    .filter("city =", "Sleman")
                    .get()

                datastore.save(
                    listOf(
                        Institution(title = "TNI AD", subtitle = "Tentara Nasional Indonesia Angkatan Darat", position = "Angkatan Darat", address = "Yogyakarta", location = location),
                        Institution(title = "TNI AU", subtitle = "Tentara Nasional Indonesia Angkatan Udara", position = "Angkatan Udara", address = "Yogyakarta", location = location),
                        Institution(title = "TNI AL", subtitle = "Tentara Nasional Indonesia Angkatan Laut", position = "Angkatan Laut", address = "Yogyakarta", location = location)
                    )

                )
            }

        }
        return operation
    }

}