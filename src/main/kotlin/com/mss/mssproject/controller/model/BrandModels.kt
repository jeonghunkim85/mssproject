package com.mss.mssproject.controller.model

import com.mss.mssproject.application.WriteBrandUseCases.WriteBrandModel
import com.mss.mssproject.domain.Brand
import jakarta.validation.constraints.NotBlank

object BrandModels {
    data class BrandView(
        val id: Long,
        val name: String,
    ) {
        constructor(brand: Brand) : this(
            id = brand.id,
            name = brand.name,
        )
    }

    data class WriteBrandDto(
        @field:NotBlank(message = "name must not be blank")
        val name: String?,
    ) {
        fun toWriteModel(): WriteBrandModel {
            requireNotNull(name)
            return WriteBrandModel(
                name = name.trim(),
            )
        }
    }
}
