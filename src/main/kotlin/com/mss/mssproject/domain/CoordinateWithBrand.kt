package com.mss.mssproject.domain

import java.math.BigDecimal

/**
 * 단일 브랜드의 모든 카테고리 상품 묶음
 */
data class CoordinateWithBrand (
    val brand: Brand,
    val products: List<Product>
) {
    val totalPrice: BigDecimal = products.sumOf { it.price }
}