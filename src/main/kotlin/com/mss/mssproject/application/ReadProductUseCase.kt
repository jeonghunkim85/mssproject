package com.mss.mssproject.application

import com.mss.mssproject.domain.Product
import com.mss.mssproject.domain.ProductsByCategory

interface ReadProductUseCase {
    fun findCheapestAndMostExpensiveProductByCategoryName(categoryName: String): ProductsByCategory

    fun readProduct(id: Long): Product
}