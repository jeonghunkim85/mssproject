package com.mss.mssproject.domain

import java.math.BigDecimal

/**
 * 단일 브랜드의 카테고리별 상품 모음
 */
data class CoordinateWithBrand(
    val brand: Brand,
    val products: List<Product>,
) {
    val totalPrice: BigDecimal = products.sumOf { it.price }
}
