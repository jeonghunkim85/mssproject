package com.mss.mssproject.application

import com.mss.mssproject.domain.Product
import java.math.BigDecimal

interface WriteProductUseCases {
    fun postProduct(product: WriteProductModel): Product

    fun putProduct(
        id: Long,
        registerModel: WriteProductModel,
    ): Product

    fun deleteProduct(id: Long)

    data class WriteProductModel(
        val categoryId: Long,
        val brandId: Long,
        val price: BigDecimal,
    )
}
