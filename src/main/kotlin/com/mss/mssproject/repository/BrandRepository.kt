package com.mss.mssproject.repository

import com.mss.mssproject.domain.Brand
import org.springframework.data.repository.CrudRepository

interface BrandRepository: CrudRepository<Brand, Long>, BrandRepositoryCustom {
    fun findByName(name: String): Brand?
}