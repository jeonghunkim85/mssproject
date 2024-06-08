package com.mss.mssproject.repository

import com.mss.mssproject.domain.Brand

interface BrandRepositoryCustom {
    fun findCheapestBrand(): Brand?
}