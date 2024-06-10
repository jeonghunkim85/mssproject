package com.mss.mssproject.domain

/**
 * 카테고리의 최저가, 최고가 상품 모음
 */
data class ProductsByCategory (
    val category: Category,
    val cheapestProduct: List<Product>,
    val mostExpensiveProduct: List<Product>,
)