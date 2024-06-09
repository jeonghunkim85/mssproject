package com.mss.mssproject.domain

data class ProductsByCategory (
    val category: Category,
    val cheapestProduct: List<Product>,
    val mostExpensiveProduct: List<Product>,
)