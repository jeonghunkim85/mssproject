package com.mss.mssproject.repository

import org.springframework.data.repository.CrudRepository
import java.util.Optional

abstract class AbstractCrudRepositoryFakeImpl<T : Any, ID : Any> : CrudRepository<T, ID> {
    protected val map: MutableMap<ID, T> = mutableMapOf()

    override fun <S : T> saveAll(entities: MutableIterable<S>): MutableIterable<S> = entities.map { save(it) }.toMutableList()

    override fun findById(id: ID): Optional<T> = Optional.ofNullable(map[id])

    override fun existsById(id: ID): Boolean = id in map

    override fun findAll(): MutableIterable<T> = map.values

    override fun findAllById(ids: MutableIterable<ID>): MutableIterable<T> {
        val idsSet = ids.toSet()
        return map.filter { it.key in idsSet }.values.toMutableList()
    }

    override fun count(): Long = map.size.toLong()

    override fun deleteById(id: ID) {
        map -= id
    }

    override fun delete(entity: T) {
        map -= map.filterValues { it == entity }.keys
    }

    override fun deleteAllById(ids: MutableIterable<ID>) {
        map -= ids.toSet()
    }

    override fun deleteAll(entities: MutableIterable<T>) {
        val entitiesSet = entities.toSet()
        map -= map.filterValues { it in entitiesSet }.keys
    }

    override fun deleteAll() {
        map.clear()
    }
}
