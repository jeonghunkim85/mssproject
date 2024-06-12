package com.mss.mssproject.application

import com.mss.mssproject.domain.Coordinate
import com.mss.mssproject.domain.CoordinateWithBrand
import com.mss.mssproject.domain.ProductsByCategory

interface CoordinateUseCases {
    /**
     * 가장 저렴한 상품들로 구성하는 코디를 검색합니다
     */
    fun findCheapestCoordinate(): Coordinate

    /**
     * 카테고리별 브랜드의 최저가 상품가격의 합이 가장 저렴한 브랜드로 구성한 코디를 검색합니다
     */
    fun findCoordinateByCheapestBrand(): CoordinateWithBrand

    /**
     * 카테고리 이름으로 해당 카테고리의 최저가/최고가 상품 가격과 브랜드를 반환합니다
     */
    fun findCheapestAndMostExpensiveProductByCategoryName(categoryName: String): ProductsByCategory
}