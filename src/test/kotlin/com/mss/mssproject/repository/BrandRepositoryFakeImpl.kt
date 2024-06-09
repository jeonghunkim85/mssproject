package com.mss.mssproject.repository

import com.mss.mssproject.domain.Brand
import org.springframework.boot.test.context.TestComponent

@TestComponent
class BrandRepositoryFakeImpl(
    private val productRepository: ProductRepository,
): BrandRepository, AbstractCrudRepositoryFakeImpl<Brand, Long>() {

    override fun findByName(name: String): Brand? =
        map.values.find { it.name == name }

    @Suppress("UNCHECKED_CAST")
    override fun <S : Brand> save(entity: S): S {
        val entityToSave = if(entity.id == 0L) {
            entity.copy(
                id = (map.keys.maxOrNull() ?: 0) + 1
            )
        } else entity
        map[entityToSave.id] = entityToSave
        return entityToSave as S
    }

    override fun findCheapestBrand(): Brand? {
        val brandPriceMap = productRepository.findAll()
            .groupBy { it.brand }
            .mapValues {
                it.value.groupBy { product ->
                    product.category
                }.mapValues { entry ->
                    entry.value.minBy {
                        product -> product.price
                    }
                }.values.sumOf {
                    product -> product.price
                }
            }
        return brandPriceMap.minBy { it.value }.key
    }
}