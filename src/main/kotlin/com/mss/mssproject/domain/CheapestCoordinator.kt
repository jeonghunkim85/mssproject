package com.mss.mssproject.domain

import java.math.BigDecimal

data class CheapestCoordinator (
    val brand: Brand,
    val products: List<Product>,
    val totalPrice: BigDecimal,
)