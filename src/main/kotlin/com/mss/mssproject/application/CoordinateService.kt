package com.mss.mssproject.application

import com.mss.mssproject.application.ProductService.Companion.MAX
import com.mss.mssproject.application.ProductService.Companion.MIN
import com.mss.mssproject.domain.Coordinate
import com.mss.mssproject.domain.CoordinateWithBrand
import com.mss.mssproject.domain.ProductsByCategory
import com.mss.mssproject.repository.BrandRepository
import com.mss.mssproject.repository.CategoryRepository
import com.mss.mssproject.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CoordinateService(
    private val brandRepository: BrandRepository,
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
): CoordinateUseCases {
    override fun findCheapestCoordinate(): Coordinate {
        val products = productRepository.findCheapestProductsByAllCategory()
        return Coordinate(products)
    }

    @Transactional(readOnly = true)
    override fun findCoordinateByCheapestBrand(): CoordinateWithBrand {
        val brand = brandRepository.findCheapestBrand()
        checkNotNull(brand) { "cannot find cheapest brand" } // 정상적인 경우는 아니라 catch 하지 않음
        val products = productRepository.findCheapestProductsByBrand(brand)
        return CoordinateWithBrand(
            brand = brand,
            products = products,
        )
    }

    @Transactional(readOnly = true)
    override fun findCheapestAndMostExpensiveProductByCategoryName(categoryName: String): ProductsByCategory {
        val category = categoryRepository.findByName(categoryName)
        checkNotNull(category) { "cannot find category $categoryName" }
        val queryResult = productRepository.findCheapestAndMostExpensiveProductsByCategory(category)
        return ProductsByCategory(
            category = category,
            cheapestProduct = queryResult
                .filter { it.first == MIN }
                .map { it.second },
            mostExpensiveProduct = queryResult
                .filter { it.first == MAX }
                .map { it.second },
        )
    }
}