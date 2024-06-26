package com.mss.mssproject.repository

import com.mss.mssproject.domain.Brand
import com.mss.mssproject.domain.Category
import com.mss.mssproject.domain.Product
import org.springframework.boot.test.context.TestComponent

@TestComponent
class ProductRepositoryFakeImpl :
    AbstractCrudRepositoryFakeImpl<Product, Long>(),
    ProductRepository {
    override fun countAllByBrandId(brandId: Long): Long = map.count { it.value.brand.id == brandId }.toLong()

    @Suppress("UNCHECKED_CAST")
    override fun <S : Product> save(entity: S): S {
        val entityToSave =
            if (entity.id == 0L) {
                entity.copy(
                    id = (map.keys.maxOrNull() ?: 0) + 1,
                )
            } else {
                entity
            }
        map[entityToSave.id] = entityToSave
        return entityToSave as S
    }

    override fun findCheapestProductsByAllCategory(): List<Product> =
        map
            .values
            .groupBy { it.category }
            .mapValues { it.value.minBy { product -> product.price } }
            .values
            .toList()

    override fun findCheapestProductsByBrand(brand: Brand): List<Product> =
        map
            .values
            .filter { it.brand == brand }
            .groupBy { it.category }
            .mapValues { it.value.minBy { product -> product.price } }
            .values
            .toList()

    override fun findCheapestAndMostExpensiveProductsByCategory(category: Category): List<Pair<String, Product>> {
        val categoryProducts = map.values.filter { it.category == category }
        val minPrice = categoryProducts.map { it.price }.min()
        val maxPrice = categoryProducts.map { it.price }.max()

        return (
            categoryProducts.filter { it.price == minPrice }.map { "MIN" to it } +
                categoryProducts.filter { it.price == maxPrice }.map { "MAX" to it }
        )
    }
}
