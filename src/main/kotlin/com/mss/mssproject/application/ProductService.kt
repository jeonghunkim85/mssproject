package com.mss.mssproject.application

import com.mss.mssproject.application.WriteProductUseCases.WriteProductModel
import com.mss.mssproject.domain.Product
import com.mss.mssproject.domain.ProductsByCategory
import com.mss.mssproject.repository.BrandRepository
import com.mss.mssproject.repository.CategoryRepository
import com.mss.mssproject.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val categoryRepository: CategoryRepository,
    private val productRepository: ProductRepository,
    private val brandRepository: BrandRepository,
): ReadProductUseCase, WriteProductUseCases {

    @Transactional(readOnly = true)
    override fun findCheapestAndMostExpensiveProductByCategoryName(categoryName: String): ProductsByCategory {
        val category = categoryRepository.findByName(categoryName)
        requireNotNull(category) { "cannot find category $categoryName" } // todo. 400 error 처리
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

    override fun readProduct(id: Long): Product =
        productRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("cannot find product(id=$id)") // todo. 404

    @Transactional(readOnly = false)
    override fun postProduct(registerModel: WriteProductModel): Product {
        val brand = brandRepository.findByIdOrNull(registerModel.brandId)
            ?: throw IllegalArgumentException("cannot find brand ${registerModel.brandId}") // todo. 400
        val category = categoryRepository.findByIdOrNull(registerModel.categoryId)
            ?: throw IllegalArgumentException("cannot find category ${registerModel.categoryId}")

        val productToPersist = Product(
            brand = brand,
            category = category,
            price = registerModel.price
        )
        return productRepository.save(productToPersist)
    }

    @Transactional(readOnly = false)
    override fun putProduct(id: Long, registerModel: WriteProductModel): Product {
        val existingProduct = productRepository.findByIdOrNull(id)
        val brand = brandRepository.findByIdOrNull(registerModel.brandId)
            ?: throw IllegalArgumentException("cannot find brand ${registerModel.brandId}")
        val category = categoryRepository.findByIdOrNull(registerModel.categoryId)
            ?: throw IllegalArgumentException("cannot find category ${registerModel.categoryId}")

        val productToPersist = existingProduct?.copy(
            price = registerModel.price,
            brand = brand,
            category = category
        ) ?: Product(
            id = id,
            price = registerModel.price,
            brand = brand,
            category = category
        )

        return productRepository.save(productToPersist)
    }

    @Transactional(readOnly = false)
    override fun deleteProduct(id: Long) {
        if(!productRepository.existsById(id)) {
            throw NoSuchElementException("cannot find product $id to delete")
        }
        productRepository.deleteById(id)
    }

    companion object {
        const val MIN = "MIN"
        const val MAX = "MAX"
    }
}