package com.mss.mssproject.application

import com.mss.mssproject.domain.Coordinate
import com.mss.mssproject.domain.CoordinateWithBrand
import com.mss.mssproject.domain.ProductsByCategory

interface CoordinateUseCases {
    fun findCheapestCoordinate(): Coordinate

    fun findCoordinateByCheapestBrand(): CoordinateWithBrand

    fun findCheapestAndMostExpensiveProductByCategoryName(categoryName: String): ProductsByCategory
}