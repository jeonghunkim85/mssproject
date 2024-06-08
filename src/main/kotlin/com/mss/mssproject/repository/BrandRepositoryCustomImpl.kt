package com.mss.mssproject.repository

import com.mss.mssproject.domain.Brand
import com.mss.mssproject.domain.QBrand.brand
import com.mss.mssproject.repository.QBrandRepositoryCustomImpl_TotalPriceRankByBrandAndCategory.totalPriceRankByBrandAndCategory
import jakarta.persistence.Entity
import jakarta.persistence.Id
import org.hibernate.annotations.Subselect
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class BrandRepositoryCustomImpl: BrandRepositoryCustom, QuerydslRepositorySupport(Brand::class.java) {

    override fun findCheapestBrand(): Brand? {
        val cheapestBrandAndTotalPrice = from(totalPriceRankByBrandAndCategory)
            .fetchFirst() ?: error("query results are not correct")

        val cheapestBrandId = cheapestBrandAndTotalPrice.brandId
        return from(brand).where(brand.id.eq(cheapestBrandId)).fetchFirst()
    }

    /**
     * 모든 카테고리 상품 중 브랜드별로 가장 저렴한 상품을 구매할 때,
     * 최저가격에 판매하는 브랜드를 조회하기 위한 subEntity
     */
    @Entity
    @Subselect("""
        select 
            brand_id, sum(price) as total_price
        from 
            (
                select 
                    id,
                    brand_id,
                    price,
                    row_number() over (partition by brand_id, category_id order by price, id desc) as min_rank
                from products
            ) a
        where a.min_rank = 1
        group by brand_id
        order by sum(price)
        limit 1
    """)
    data class TotalPriceRankByBrandAndCategory(
        @Id
        val brandId: Long,
        val totalPrice: BigDecimal
    )
}