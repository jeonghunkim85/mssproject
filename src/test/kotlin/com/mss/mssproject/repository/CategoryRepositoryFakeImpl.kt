package com.mss.mssproject.repository

import com.mss.mssproject.domain.Brand
import com.mss.mssproject.domain.Category
import org.springframework.boot.test.context.TestComponent

@TestComponent
class CategoryRepositoryFakeImpl: CategoryRepository, AbstractCrudRepositoryFakeImpl<Category, Long>() {
    override fun findByName(name: String): Category? = map.values.find { it.name == name }

    @Suppress("UNCHECKED_CAST")
    override fun <S : Category> save(entity: S): S {
        val entityToSave = if(entity.id == 0L) {
            entity.copy(
                id = (map.keys.maxOrNull() ?: 0) + 1
            )
        } else entity
        map[entityToSave.id] = entityToSave
        return entityToSave as S
    }
}