package com.mss.mssproject.controller.mapper

import com.mss.mssproject.controller.model.CoordinateModels
import com.mss.mssproject.domain.Coordinate
import com.mss.mssproject.domain.CoordinateWithBrand
import com.mss.mssproject.extension.toFormattedString

fun Coordinate.toDto() = CoordinateModels.CoordinateView(
    products = products.map { it.toDtoWithBrand() },
    totalPrice = totalPrice.toFormattedString()
)

fun CoordinateWithBrand.toDto() = CoordinateModels.CoordinateWithBrandView(
    brandName = brand.name,
    products = products.map { it.toDto() },
    totalPrice = totalPrice.toFormattedString()
)