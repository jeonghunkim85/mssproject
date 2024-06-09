package com.mss.mssproject.domain

import java.math.BigDecimal

/**
 * 카테고리별 최저가 상품 모음
 */
data class Coordinate (
    val products: List<Product>
) {
    val totalPrice: BigDecimal = products.sumOf { it.price }
}