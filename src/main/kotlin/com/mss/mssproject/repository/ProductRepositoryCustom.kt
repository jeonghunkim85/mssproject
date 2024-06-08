package com.mss.mssproject.repository

import com.mss.mssproject.domain.*

interface ProductRepositoryCustom {
    fun findCheapestProductsByAllCategory(): List<Product>

    fun findCheapestProductsByBrand(brand: Brand): List<Product>

    fun findCheapestAndMostExpensiveProductsByCategory(category: Category): ProductsByCategory
}