package com.mss.mssproject.controller.model

import com.mss.mssproject.application.WriteProductUseCases.WriteProductModel
import com.mss.mssproject.domain.Product
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

object ProductModels {
    data class ProductView(
        val id: Long,
        val brandId: Long,
        val categoryId: Long,
        val price: BigDecimal,
    ) {
        constructor(product: Product): this(
            id = product.id,
            brandId = product.brand.id,
            categoryId = product.category.id,
            price = product.price,
        )
    }

    data class WriteProductDto(
        @field:NotNull(message = "brandId 는 필수입니다.")
        @field:Positive(message = "brandId 는 양수만 입력 가능합니다.")
        val brandId: Long?,

        @field:NotNull(message = "categoryId 는 필수입니다.")
        @field:Positive(message = "categoryId 는 양수만 입력 가능합니다.")
        val categoryId: Long?,

        @field:NotNull(message = "price 는 필수입니다.")
        @field:PositiveOrZero(message = "price 는 0 이상만 입력 가능합니다.")
        val price: BigDecimal?,
    ) {
        fun toWriteModel(): WriteProductModel {
            requireNotNull(brandId) // logically cannot be null
            requireNotNull(categoryId) // logically cannot be null
            requireNotNull(price) // logically cannot be null
            return WriteProductModel(
                brandId = brandId,
                categoryId = categoryId,
                price = price,
            )
        }
    }
}