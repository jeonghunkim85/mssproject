package com.mss.mssproject.repository

import com.mss.mssproject.domain.Brand
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface BrandRepository: CrudRepository<Brand, Long> {
    fun findByName(name: String): Brand?

    @Query("""
        select z.brand
            from (
                select p.id as id,
                       p.brand as brand,
                       p.price as price,
                       row_number() over (partition by p.brand, p.category order by p.price, p.id desc) as min_rank
                from Product p
            ) z
            where z.min_rank = 1
            group by z.brand
            order by sum(z.price)
            limit 1
    """)
    fun findCheapestBrand(): Brand?
}