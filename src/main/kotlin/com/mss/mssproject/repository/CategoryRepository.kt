package com.mss.mssproject.repository

import com.mss.mssproject.domain.Category
import org.springframework.data.repository.CrudRepository

interface CategoryRepository: CrudRepository<Category, Long> {
    fun findByName(name: String): Category?
}