package com.mss.mssproject.controller.mapper

import com.mss.mssproject.controller.model.CoordinateModels
import com.mss.mssproject.domain.Product
import com.mss.mssproject.extension.toFormattedString

fun Product.toDtoWithBrand() = CoordinateModels.ProductWithBrandView(
    categoryName = category.name,
    brandName = brand.name,
    price = price.toFormattedString()
)

fun Product.toDto() = CoordinateModels.ProductView(
    categoryName = category.name,
    price = price.toFormattedString(),
)