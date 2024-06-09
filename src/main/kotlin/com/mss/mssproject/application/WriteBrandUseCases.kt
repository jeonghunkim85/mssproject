package com.mss.mssproject.application

import com.mss.mssproject.domain.Brand

interface WriteBrandUseCases {
    fun postBrand(brand: WriteBrandModel): Brand

    fun putBrand(id: Long, brand: WriteBrandModel): Brand

    fun deleteBrand(id: Long)

    data class WriteBrandModel(
        val name: String,
    ) {
        fun toBrand(id: Long = 0) = Brand(
            id = id,
            name = name,
        )
    }
}