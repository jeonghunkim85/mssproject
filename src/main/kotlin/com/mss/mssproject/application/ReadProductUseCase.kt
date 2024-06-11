package com.mss.mssproject.application

import com.mss.mssproject.domain.Product

interface ReadProductUseCase {
    fun readProduct(id: Long): Product
}