package com.mss.mssproject.repository

import com.mss.mssproject.domain.Product
import org.springframework.data.repository.CrudRepository

interface ProductRepository: CrudRepository<Product, Long>, ProductRepositoryCustom {
    fun countAllByBrandId(brandId: Long): Long
}