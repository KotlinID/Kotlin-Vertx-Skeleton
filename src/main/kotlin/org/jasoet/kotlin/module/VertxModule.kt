package org.jasoet.kotlin.module

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import dagger.Module
import dagger.Provides
import io.vertx.core.Vertx
import io.vertx.core.eventbus.EventBus
import io.vertx.core.file.FileSystem
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.Router
import io.vertx.ext.web.templ.PebbleTemplateEngine
import io.vertx.ext.web.templ.TemplateEngine
import org.jasoet.kotlin.extension.logger
import javax.inject.Named
import javax.inject.Singleton
import javax.validation.Validation
import javax.validation.Validator

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */

@Module
class VertxModule(val vertx: Vertx, val config: JsonObject) {
    private val log = logger(VertxModule::class)

    init {
        Json.mapper.apply {
            registerKotlinModule()
            registerModule(ParameterNamesModule())
            registerModule(JavaTimeModule())
        }

        Json.prettyMapper.apply {
            registerKotlinModule()
            registerModule(ParameterNamesModule())
            registerModule(JavaTimeModule())
        }
    }

    @Provides
    fun provideRouter(): Router {
        return Router.router(vertx)
    }

    @Singleton
    @Provides
    fun provideEventBus(): EventBus {
        return vertx.eventBus()
    }

    @Provides
    @Singleton
    fun provideVertx(): Vertx {
        return vertx
    }

    @Provides
    @Singleton
    fun provideObjectMapper(): ObjectMapper {
        return Json.mapper
    }

    @Provides
    @Singleton
    fun provideFileSystem(): FileSystem {
        return vertx.fileSystem()
    }

    @Provides
    @Singleton
    @Named("imageLocation")
    fun imageLocation(): String {
        return try {
            config.getString("IMAGE_LOCATION") ?: throw IllegalArgumentException("image.location config is not defined")
        } catch (e: Exception) {
            log.info("Initialize IMAGE_LOCATION failed! ${e.message}", e)
            throw e
        }
    }

    @Provides
    @Singleton
    @Named("baseUrl")
    fun baseUrl(): String {
        val url = try {
            config.getString("BASE_URL") ?: throw IllegalArgumentException("base.url config is not defined")
        } catch (e: Exception) {
            log.info("Initialize BASE_URL failed ${e.message}", e)
            throw e
        }
        return if (url.endsWith("/")) {
            url
        } else {
            "$url/"
        }

    }

    @Provides
    @Singleton
    fun providePebbleTemplate(): TemplateEngine {
        return PebbleTemplateEngine.create(vertx)
    }

    @Provides
    @Singleton
    fun provideValidator(): Validator {
        val validatorFactory = Validation.buildDefaultValidatorFactory()
        return validatorFactory.validator
    }
}