package com.mss.mssproject.controller

import com.fasterxml.jackson.annotation.JsonProperty
import com.mss.mssproject.application.CoordinateUseCases
import com.mss.mssproject.controller.mapper.toDto
import com.mss.mssproject.controller.mapper.toDtoWithBrand
import com.mss.mssproject.controller.model.CoordinateModels.CoordinateView
import com.mss.mssproject.controller.model.CoordinateModels.CoordinateWithBrandView
import com.mss.mssproject.controller.model.CoordinateModels.ProductWithBrandView
import com.mss.mssproject.domain.ProductsByCategory
import com.mss.mssproject.exception.BadRequestException
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/coordinates")
@RestController
class CoordinateController(
    private val coordinateUseCases: CoordinateUseCases
) {

    @Operation(summary = "카테고리별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API")
    @GetMapping("/cheapest")
    fun getCheapest(): CoordinateViewWrapper<CoordinateView> {
        val coordinate = coordinateUseCases.findCheapestCoordinate()
        return CoordinateViewWrapper(coordinate.toDto())
    }

    @Operation(
        summary = "단일 브랜드 카테고리별 최저 상품 가격, 총액 조회 API",
        description = "단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와" +
                "카테고리의 상품가격, 총액을 조회하는 API"
    )
    @GetMapping("/cheapest-by-brand")
    fun getCheapestByBrand(): CoordinateViewWrapper<CoordinateWithBrandView> {
        val coordinate = coordinateUseCases.findCoordinateByCheapestBrand()
        return CoordinateViewWrapper(coordinate.toDto())
    }

    @Operation(summary = "카테고리를 기준으로 최저, 최고 가격 브랜드와 상품가격을 조회하는 API")
    @GetMapping("/cheapest-and-most-expensive")
    fun findCheapestAndMostExpensiveProductByCategory(
        @RequestParam(name = "category_name", required = true)
        categoryName: String?,
    ) : CheapestAndMostExpensiveProductByCategoryView {
        if(categoryName.isNullOrBlank()) {
            throw BadRequestException("category_name must not be blank")
        }
        val result = try {
            coordinateUseCases.findCheapestAndMostExpensiveProductByCategoryName(categoryName)
        }catch (ise: IllegalStateException) {
            throw BadRequestException(ise.message)
        }
        return CheapestAndMostExpensiveProductByCategoryView(result)
    }

    data class CoordinateViewWrapper<T>(
        @JsonProperty("최저가")
        val cheapest: T
    )

    data class CheapestAndMostExpensiveProductByCategoryView(
        @JsonProperty("카테고리")
        val categoryName: String,
        @JsonProperty("최저가")
        val cheapestProduct: List<ProductWithBrandView>,
        @JsonProperty("최고가")
        val mostExpensiveProduct: List<ProductWithBrandView>,
    ) {
        constructor(productsByCategory: ProductsByCategory): this(
            categoryName = productsByCategory.category.name,
            cheapestProduct = productsByCategory.cheapestProduct.map { it.toDtoWithBrand() },
            mostExpensiveProduct = productsByCategory.mostExpensiveProduct.map { it.toDtoWithBrand() }
        )
    }
}