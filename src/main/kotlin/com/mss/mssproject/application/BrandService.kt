package com.mss.mssproject.application

import com.mss.mssproject.application.WriteBrandUseCases.WriteBrandModel
import com.mss.mssproject.domain.Brand
import com.mss.mssproject.repository.BrandRepository
import com.mss.mssproject.repository.ProductRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BrandService(
    private val brandRepository: BrandRepository,
    private val productRepository: ProductRepository,
): ReadBrandUseCases, WriteBrandUseCases {
    override fun getBrandByName(name: String): Brand =
        brandRepository.findByName(name)
            ?: throw NoSuchElementException("cannot find brand(name=$name)")

    override fun getBrand(id: Long): Brand =
        brandRepository.findByIdOrNull(id)
            ?: throw NoSuchElementException("cannot find brand(id=$id)")

    override fun postBrand(brand: WriteBrandModel): Brand =
        brandRepository.save(brand.toBrand())

    @Transactional(readOnly = false)
    override fun putBrand(id: Long, brand: WriteBrandModel): Brand {
        val existingBrand = brandRepository.findByIdOrNull(id)
        val brandToPersist = existingBrand?.copy(
            name = brand.name
        ) ?: brand.toBrand(id = id)
        return brandRepository.save(brandToPersist)
    }

    @Transactional(readOnly = false)
    override fun deleteBrand(id: Long) {
        if(!brandRepository.existsById(id)) {
            throw NoSuchElementException("cannot find brand(id=$id)")
        }

        val existingProductsCount = productRepository.countAllByBrandId(id)
        if(existingProductsCount > 0) {
            throw IllegalStateException("there are $existingProductsCount products persisted. cannot delete.")
        }

        brandRepository.deleteById(id)
    }
}