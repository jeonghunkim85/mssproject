package com.mss.mssproject.repository

import com.mss.mssproject.domain.Brand
import com.mss.mssproject.domain.Category
import com.mss.mssproject.domain.Product
import com.mss.mssproject.domain.ProductsByCategory
import com.mss.mssproject.domain.QProduct.product
import com.mss.mssproject.repository.QProductRepositoryCustomImpl_CheapestAndExpensivePriceRankByCategory.cheapestAndExpensivePriceRankByCategory
import com.mss.mssproject.repository.QProductRepositoryCustomImpl_CheapestPriceRankByCategory.cheapestPriceRankByCategory
import com.mss.mssproject.repository.QProductRepositoryCustomImpl_PriceRankByBrandCategory.priceRankByBrandCategory
import com.querydsl.core.types.dsl.Expressions
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.Subselect
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryCustomImpl: ProductRepositoryCustom, QuerydslRepositorySupport(Product::class.java) {

    override fun findCheapestProductsByAllCategory(): List<Product> {
        return from(product)
            .join(cheapestPriceRankByCategory)
            .on(product.id.eq(cheapestPriceRankByCategory.id))
            .where(cheapestPriceRankByCategory.minRank.eq(FIRST_RANK))
            .orderBy(product.category.id.asc())
            .fetch()
    }

    override fun findCheapestProductsByBrand(brand: Brand): List<Product> {
        val products = from(product)
            .join(priceRankByBrandCategory)
            .on(product.id.eq(priceRankByBrandCategory.id))
            .where(
                priceRankByBrandCategory.minRank.eq(FIRST_RANK)
                    .and(product.brand.eq(brand))
            )
            .fetch()
        return products
    }

    override fun findCheapestAndMostExpensiveProductsByCategory(category: Category): ProductsByCategory {
        val rank = cheapestAndExpensivePriceRankByCategory
        val queryResult = from(product)
            .select(
                Expressions.cases()
                    .`when`(rank.minRank.eq(FIRST_RANK)).then(MIN)
                    .`when`(rank.maxRank.eq(FIRST_RANK)).then(MAX)
                    .otherwise(UNKNOWN),
                product,
            )
            .join(rank)
            .on(product.id.eq(rank.id))
            .where(
                product.category.eq(category)
                    .and(
                        rank.minRank.eq(FIRST_RANK)
                            .or(rank.maxRank.eq(FIRST_RANK))
                    )
            )
            .fetch()

        val cheapest = queryResult
            .filter { it.get(0, String::class.java) == MIN }
            .mapNotNull { it.get(1, Product::class.java) }

        val mostExpensive = queryResult
            .filter { it.get(0, String::class.java) == MAX }
            .mapNotNull { it.get(1, Product::class.java) }

        return ProductsByCategory(
            category = category,
            cheapestProduct = cheapest,
            mostExpensiveProduct = mostExpensive,
        )
    }

    companion object {
        private const val FIRST_RANK = 1L
        private const val MIN = "MIN"
        private const val MAX = "MAX"
        private const val UNKNOWN = "UNKNOWN"
    }

    /**
     * category 별 가격 순 rank 계산을 위한 subEntity
     */
    @Entity
    @Subselect("""
        select 
            id,
            row_number() over(partition by category_id order by price, id desc) as min_rank
        from products 
    """)
    data class CheapestPriceRankByCategory(
        @Id
        val id: Long,
        val minRank: Long,
    )

    @Entity
    @Subselect("""
        select 
            id,
            dense_rank() over(partition by category_id order by price) as min_rank,
            dense_rank() over(partition by category_id order by price desc) as max_rank
        from products 
    """)
    data class CheapestAndExpensivePriceRankByCategory(
        @Id
        val id: Long,
        val minRank: Long,
        val maxRank: Long,
    )

    /**
     * 한 브랜드에 해당하는 상품들 중 가장 저렴한 상품들을 가져오기 위한 subEntity
     */
    @Entity
    @Subselect("""
        select 
            id,
            row_number() over(partition by brand_id, category_id order by price, id desc) as min_rank
        from products 
    """)
    data class PriceRankByBrandCategory(
        @Id
        val id: Long,
        val minRank: Long,
    )
}