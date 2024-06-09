package com.mss.mssproject.application

import com.mss.mssproject.domain.Brand

interface ReadBrandUseCases {
    fun getBrandByName(name: String): Brand

    fun getBrand(id: Long): Brand
}