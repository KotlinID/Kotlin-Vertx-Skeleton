package org.jasoet.kotlin.extension

import com.mongodb.WriteResult
import io.vertx.core.json.JsonObject
import org.mongodb.morphia.Datastore
import org.mongodb.morphia.Key
import org.mongodb.morphia.query.Query
import org.mongodb.morphia.query.UpdateOperations

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */


inline fun <reified T : Any> Datastore.createQuery(): Query<T> {
    return this.createQuery(T::class.java)
}

inline fun <reified T : Any> Datastore.createUpdateOperations(): UpdateOperations<T> {
    return this.createUpdateOperations(T::class.java)
}

inline fun <reified T : Any, V : Any> Datastore.deleteById(id: V): WriteResult {
    return this.delete(T::class.java, id)
}

inline fun <reified T : Any, V : Any> Datastore.deleteByIds(ids: Iterable<V>): WriteResult {
    return this.delete(T::class.java, ids)
}

inline fun <reified T : Any> Datastore.count(): Long {
    return this.getCount(T::class.java)
}

inline fun <reified T : Any> Datastore.count(query: Query<T>): Long {
    return this.getCount(query)
}


inline fun <reified T : Any> Datastore.getById(id: Any?): T? {
    return this.get(T::class.java, id)
}

inline fun <reified T : Any> Datastore.getByIds(id: Iterable<Any>): Query<T> {
    return this.get(T::class.java, id)
}

inline fun <reified T : Any> Datastore.getByKey(id: Key<T>): T? {
    return this.getByKey(T::class.java, id)
}

inline fun <reified T : Any> Datastore.getByKeyList(ids: Iterable<Key<T>>): List<T> {
    return this.getByKeys(T::class.java, ids) ?: emptyList()
}

fun <T : Any> Datastore.refresh(id: T): T? {
    return this.get(id)
}

inline fun <reified T : Any> Datastore.find(): Query<T> {
    return this.find(T::class.java)
}

inline fun <reified T : Any> Datastore.updateOperation(): UpdateOperations<T> {
    return this.createUpdateOperations(T::class.java)
}

fun <T : Any> UpdateOperations<T>.setIfNotNull(field: String, value: Any?): UpdateOperations<T> {
    if (value !== null) {
        return this.set(field, value)
    } else {
        return this
    }
}

inline fun <reified T : Any> UpdateOperations<T>.byJson(json: JsonObject): UpdateOperations<T> {
    var self = this
    json.forEach {
        val (key, value) = it
        self = self.set(key, value)
    }
    return self
}





